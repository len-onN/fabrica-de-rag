package local.fabricarag.core.port.out;

import local.fabricarag.core.dto.rag.ContextItemResponse;

import java.util.List;

public interface AnswerProviderPort {
    AnswerProviderResponse generateAnswer(String query, List<ContextItemResponse> context);
}
