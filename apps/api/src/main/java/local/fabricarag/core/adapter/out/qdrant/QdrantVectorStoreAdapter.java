package local.fabricarag.core.adapter.out.qdrant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.ChunkEmbedding;
import local.fabricarag.core.domain.Document;
import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.domain.VectorIndexBinding;
import local.fabricarag.core.port.out.VectorStorePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;

@Component
public class QdrantVectorStoreAdapter implements VectorStorePort {

    private final QdrantClient qdrantClient;
    private final ObjectMapper objectMapper;

    public QdrantVectorStoreAdapter(QdrantClient qdrantClient, ObjectMapper objectMapper) {
        this.qdrantClient = qdrantClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void upsert(ChunkEmbedding embedding, Chunk chunk, Document document, KnowledgeCollection collection, VectorIndexBinding binding, List<Float> vector) {
        try {
            Map<String, Object> sourceLocator = objectMapper.readValue(chunk.getSourceLocator(), new TypeReference<>() {});
            List<String> headingPath = null;
            if (chunk.getHeadingPath() != null) {
                headingPath = objectMapper.readValue(chunk.getHeadingPath(), new TypeReference<>() {});
            }

            Integer filePage = (Integer) sourceLocator.get("filePageNumber");
            String printedLabel = (String) sourceLocator.get("printedLabel");

            String collectionName = binding.getRemoteCollectionName();
            if (collectionName == null || collectionName.isEmpty()) {
                collectionName = "ragcreator_chunks_v1";
            }

            Points.PointId pointId = id(UUID.fromString(embedding.getQdrantPointId()));

            java.util.Map<String, JsonWithInt.Value> payload = new java.util.HashMap<>();
            payload.put("payloadContractVersion", value(embedding.getPayloadContractVersion()));
            payload.put("workspaceId", value(embedding.getWorkspaceId()));
            payload.put("collectionId", value(collection.getPublicId()));
            payload.put("documentId", value(document.getPublicId()));
            payload.put("chunkId", value(chunk.getPublicId()));
            payload.put("contentKind", value(chunk.getContentKind()));
            payload.put("embeddingModel", value(embedding.getEmbeddingModel()));
            payload.put("embeddingModelVersion", value(embedding.getEmbeddingModelVersion()));
            payload.put("vectorSpace", value(embedding.getVectorSpace()));
            payload.put("sourceType", value(document.getSourceType()));
            payload.put("sourceHash", value(chunk.getSourceHash()));
            payload.put("chunkingVersion", value(chunk.getChunkingVersion()));

            if (filePage != null) {
                payload.put("filePageStart", value((long) filePage));
                payload.put("filePageEnd", value((long) filePage));
            }
            if (printedLabel != null) {
                payload.put("printedPageStart", value(printedLabel));
                payload.put("printedPageEnd", value(printedLabel));
            }
            if (headingPath != null) {
                // Manually convert list of strings to list of Values if value() doesn't support list directly
                JsonWithInt.ListValue.Builder listBuilder = JsonWithInt.ListValue.newBuilder();
                for (String heading : headingPath) {
                    listBuilder.addValues(value(heading));
                }
                payload.put("headingPath", JsonWithInt.Value.newBuilder().setListValue(listBuilder).build());
            }

            Points.PointStruct point = Points.PointStruct.newBuilder()
                    .setId(pointId)
                    .setVectors(Points.Vectors.newBuilder().setVector(
                            Points.Vector.newBuilder().addAllData(vector).build()
                    ).build())
                    .putAllPayload(payload)
                    .build();

            qdrantClient.upsertAsync(collectionName, List.of(point)).get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to upsert to Qdrant", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to upsert to Qdrant", e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payload for Qdrant", e);
        }
    }

    @Override
    public void upsertBatch(List<ChunkEmbedding> embeddings, List<Chunk> chunks, Document document, KnowledgeCollection collection, VectorIndexBinding binding, List<List<Float>> vectors) {
        try {
            String collectionName = binding != null ? binding.getRemoteCollectionName() : "ragcreator_chunks_v1";
            if (collectionName == null || collectionName.isEmpty()) {
                collectionName = "ragcreator_chunks_v1";
            }
            
            List<Points.PointStruct> points = new java.util.ArrayList<>();

            for (int i = 0; i < embeddings.size(); i++) {
                ChunkEmbedding embedding = embeddings.get(i);
                Chunk chunk = chunks.get(i);
                List<Float> vector = vectors.get(i);
                
                Map<String, Object> sourceLocator = objectMapper.readValue(chunk.getSourceLocator(), new TypeReference<>() {});
                List<String> headingPath = null;
                if (chunk.getHeadingPath() != null) {
                    headingPath = objectMapper.readValue(chunk.getHeadingPath(), new TypeReference<>() {});
                }

                Integer filePage = (Integer) sourceLocator.get("filePageNumber");
                String printedLabel = (String) sourceLocator.get("printedLabel");

                Points.PointId pointId = id(UUID.fromString(embedding.getQdrantPointId()));

                java.util.Map<String, JsonWithInt.Value> payload = new java.util.HashMap<>();
                payload.put("payloadContractVersion", value(embedding.getPayloadContractVersion()));
                payload.put("workspaceId", value(embedding.getWorkspaceId()));
                if (collection != null) {
                    payload.put("collectionId", value(collection.getPublicId()));
                }
                payload.put("documentId", value(document.getPublicId()));
                payload.put("chunkId", value(chunk.getPublicId()));
                payload.put("contentKind", value(chunk.getContentKind()));
                payload.put("embeddingModel", value(embedding.getEmbeddingModel()));
                payload.put("embeddingModelVersion", value(embedding.getEmbeddingModelVersion()));
                payload.put("vectorSpace", value(embedding.getVectorSpace()));
                payload.put("sourceType", value(document.getSourceType()));
                payload.put("sourceHash", value(chunk.getSourceHash()));
                if (chunk.getChunkingVersion() != null) {
                    payload.put("chunkingVersion", value(chunk.getChunkingVersion()));
                }

                if (filePage != null) {
                    payload.put("filePageStart", value((long) filePage));
                    payload.put("filePageEnd", value((long) filePage));
                }
                if (printedLabel != null) {
                    payload.put("printedPageStart", value(printedLabel));
                    payload.put("printedPageEnd", value(printedLabel));
                }
                if (headingPath != null) {
                    JsonWithInt.ListValue.Builder listBuilder = JsonWithInt.ListValue.newBuilder();
                    for (String heading : headingPath) {
                        listBuilder.addValues(value(heading));
                    }
                    payload.put("headingPath", JsonWithInt.Value.newBuilder().setListValue(listBuilder).build());
                }

                Points.PointStruct point = Points.PointStruct.newBuilder()
                        .setId(pointId)
                        .setVectors(Points.Vectors.newBuilder().setVector(
                                Points.Vector.newBuilder().addAllData(vector).build()
                        ).build())
                        .putAllPayload(payload)
                        .build();
                        
                points.add(point);
            }

            if (!points.isEmpty()) {
                qdrantClient.upsertAsync(collectionName, points).get();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to batch upsert to Qdrant", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to batch upsert to Qdrant", e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process batch payload for Qdrant", e);
        }
    }

    @Override
    public void delete(VectorIndexBinding binding, String qdrantPointId) {
        try {
            String collectionName = binding.getRemoteCollectionName();
            if (collectionName == null || collectionName.isEmpty()) {
                collectionName = "ragcreator_chunks_v1";
            }
            Points.PointId pointId = id(UUID.fromString(qdrantPointId));
            qdrantClient.deleteAsync(collectionName, List.of(pointId)).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete from Qdrant", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to delete from Qdrant", e.getCause());
        }
    }
}
