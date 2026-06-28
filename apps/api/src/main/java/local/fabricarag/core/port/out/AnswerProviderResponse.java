package local.fabricarag.core.port.out;

import java.util.List;

public record AnswerProviderResponse(
        String status,
        String answer,
        List<String> citationIds,
        String reason
) {}
