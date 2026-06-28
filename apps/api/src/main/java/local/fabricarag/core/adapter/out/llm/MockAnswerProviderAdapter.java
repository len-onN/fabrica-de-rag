package local.fabricarag.core.adapter.out.llm;

import local.fabricarag.core.dto.rag.ContextItemResponse;
import local.fabricarag.core.port.out.AnswerProviderPort;
import local.fabricarag.core.port.out.AnswerProviderResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MockAnswerProviderAdapter implements AnswerProviderPort {

    @Override
    public AnswerProviderResponse generateAnswer(String query, List<ContextItemResponse> context) {
        if (context == null || context.isEmpty()) {
            return new AnswerProviderResponse(
                    "insufficient_evidence",
                    null,
                    Collections.emptyList(),
                    "no_reliable_context"
            );
        }

        // Simula uma resposta afirmativa fundamentada no primeiro item do contexto
        String firstCitationId = context.get(0).citationId();
        
        String answer = "Esta é uma resposta RAG simulada e fundamentada. A query enviada foi: '" + query + "'. " +
                        "Baseado no contexto fornecido, a principal fonte utilizada foi " + firstCitationId + ".";

        return new AnswerProviderResponse(
                "success",
                answer,
                List.of(firstCitationId),
                null
        );
    }
}
