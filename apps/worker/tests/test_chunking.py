import json
import pytest
from rag_worker.contracts.chunks import ChunksBuildRequest
from rag_worker.services.chunking import build_chunks_sync

def test_chunking_with_fixtures():
    # Load request fixture
    with open("../../tests/contracts/worker/chunks-build.request.v1.json", "r") as f:
        req_data = json.load(f)
        
    req = ChunksBuildRequest(**req_data)
    
    # Process
    res = build_chunks_sync(req)
    
    assert res.requestId == "req_fixture_003"
    assert res.workspaceId == "wsp_fixture_alpha"
    assert len(res.chunks) >= 2
    
    # The first chunk should be the intro text
    assert res.chunks[0].contentKind == "text"
    assert "Pagina impressa e pagina fisica" in res.chunks[0].content
    
    # Check that a relation was created (since there's more than one chunk)
    assert len(res.relations) > 0
    assert res.relations[0].fromChunkId == res.chunks[0].chunkId
    assert res.relations[0].toChunkId == res.chunks[1].chunkId
    
    # Check metrics
    assert res.metrics.inputBlocks == 4
    assert res.metrics.chunksCreated == len(res.chunks)
