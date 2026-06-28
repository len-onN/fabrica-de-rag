import hashlib
import json
import logging
import uuid
from typing import List, Optional

from rag_worker.contracts.chunks import (
    ChunksBuildRequest,
    ChunksBuildResponse,
    Chunk,
    ChunkRelation,
    ChunkMetrics,
    ChunkElement,
    ChunkPage,
    VisualInterpretation
)

logger = logging.getLogger(__name__)

def generate_source_hash(content: str, locator_dict: dict, version: str) -> str:
    """Generates a deterministic hash for a chunk."""
    raw = f"{version}:{content}:{json.dumps(locator_dict, sort_keys=True)}"
    return "sha256:" + hashlib.sha256(raw.encode('utf-8')).hexdigest()

def estimate_tokens(text: str) -> int:
    """Simple word-count based token estimation for MVP."""
    return int(len(text.split()) * 1.3) if text else 0

def build_chunks_sync(request: ChunksBuildRequest) -> ChunksBuildResponse:
    logger.info(f"Building chunks for document {request.documentId}")

    chunks: List[Chunk] = []
    relations: List[ChunkRelation] = []
    
    # Sort elements by reading order
    elements = sorted(request.elements, key=lambda e: e.readingOrder)
    
    current_heading_path: List[str] = []
    current_text_accumulator = ""
    current_elements: List[ChunkElement] = []
    current_tokens = 0
    sequence_number = 1
    
    def flush_accumulator():
        nonlocal current_text_accumulator, current_elements, current_tokens, sequence_number, current_heading_path
        if not current_text_accumulator.strip():
            return
            
        # Basic chunk creation
        # We just take the source locator of the first element for simplicity in MVP
        if not current_elements:
            return
            
        first_locator = current_elements[0].sourceLocator
        
        chunk = Chunk(
            chunkId=f"chk_gen_{uuid.uuid4().hex[:8]}",
            sequenceNumber=sequence_number,
            contentKind="text",
            content=current_text_accumulator.strip(),
            headingPath=list(current_heading_path),
            tokenCount=current_tokens,
            sourceHash=generate_source_hash(current_text_accumulator, first_locator.model_dump(), request.chunkingVersion),
            sourceLocator=first_locator
        )
        chunks.append(chunk)
        sequence_number += 1
        
        # Overlap logic (simple approach: keep last few words)
        words = current_text_accumulator.split()
        overlap_words = words[-int(request.overlapTokens / 1.3):] if request.overlapTokens > 0 else []
        
        current_text_accumulator = " ".join(overlap_words) + " " if overlap_words else ""
        current_tokens = estimate_tokens(current_text_accumulator)
        current_elements = [] # overlap elements are tricky, we'll just start fresh for locator

    for el in elements:
        if el.elementType == "heading":
            # Update heading path (simple heuristic: clear path if it's a major heading)
            if current_heading_path:
                current_heading_path.pop()
            if el.textContent:
                current_heading_path.append(el.textContent)
            continue
            
        if el.elementType == "table":
            flush_accumulator()
            table_content = el.textContent or ""
            chunk = Chunk(
                chunkId=f"chk_table_{uuid.uuid4().hex[:8]}",
                sequenceNumber=sequence_number,
                contentKind="table_text",
                content=table_content,
                headingPath=list(current_heading_path),
                tokenCount=estimate_tokens(table_content),
                sourceHash=generate_source_hash(table_content, el.sourceLocator.model_dump(), request.chunkingVersion),
                sourceLocator=el.sourceLocator
            )
            chunks.append(chunk)
            sequence_number += 1
            continue
            
        # Accumulate text block
        text = (el.textContent or "").strip()
        if not text:
            continue
            
        tokens = estimate_tokens(text)
        if current_tokens + tokens > request.targetTokens and current_tokens > 0:
            flush_accumulator()
            
        current_text_accumulator += text + " "
        current_tokens += tokens
        current_elements.append(el)
        
    flush_accumulator()
    
    # Process visual interpretations
    for vis in request.visualInterpretations:
        text = vis.interpretationText
        chunk = Chunk(
            chunkId=f"chk_vis_{uuid.uuid4().hex[:8]}",
            sequenceNumber=sequence_number,
            contentKind="visual_interpretation",
            content=text,
            headingPath=[],
            tokenCount=estimate_tokens(text),
            sourceHash=generate_source_hash(text, vis.sourceLocator.model_dump(), request.chunkingVersion),
            sourceLocator=vis.sourceLocator
        )
        chunks.append(chunk)
        sequence_number += 1

    # Create next/previous relations
    for i in range(len(chunks) - 1):
        relations.append(ChunkRelation(
            fromChunkId=chunks[i].chunkId,
            toChunkId=chunks[i+1].chunkId,
            relationType="next"
        ))
        
    metrics = ChunkMetrics(
        inputBlocks=len(elements) + len(request.visualInterpretations),
        chunksCreated=len(chunks),
        estimatedTokens=sum(c.tokenCount for c in chunks)
    )
    
    return ChunksBuildResponse(
        requestId=request.requestId,
        workspaceId=request.workspaceId,
        documentId=request.documentId,
        chunkingStrategy=request.chunkingStrategy,
        chunkingVersion=request.chunkingVersion,
        chunks=chunks,
        relations=relations,
        metrics=metrics
    )

async def build_chunks_and_callback(request: ChunksBuildRequest):
    """Async wrapper for background execution."""
    import httpx
    try:
        result = build_chunks_sync(request)
        if request.callbackUrl:
            async with httpx.AsyncClient() as client:
                await client.post(request.callbackUrl, json=result.model_dump())
    except Exception as e:
        logger.error(f"Error in background chunking: {e}")
