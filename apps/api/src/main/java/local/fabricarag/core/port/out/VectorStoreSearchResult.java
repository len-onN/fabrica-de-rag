package local.fabricarag.core.port.out;

import java.util.Map;

public record VectorStoreSearchResult(
        String pointId,
        double score,
        Map<String, Object> payload
) {
}
