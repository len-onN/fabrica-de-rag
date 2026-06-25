package local.fabricarag.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE_URL = "https://fabricarag.local/problems/";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request contains invalid fields.");
        problemDetail.setType(URI.create(PROBLEM_BASE_URL + "validation-error"));
        problemDetail.setTitle("Validation error");
        
        // Add custom properties
        problemDetail.setProperty("code", "validation_error");
        problemDetail.setProperty("correlationId", generateCorrelationId());
        
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(
                        error.getField(),
                        error.getCode() != null ? error.getCode().toLowerCase() : "invalid",
                        error.getDefaultMessage()
                ))
                .toList();
        
        problemDetail.setProperty("fieldErrors", fieldErrors);
        
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problemDetail.setType(URI.create(PROBLEM_BASE_URL + "internal-server-error"));
        problemDetail.setTitle("Internal server error");
        
        problemDetail.setProperty("code", "internal_server_error");
        problemDetail.setProperty("correlationId", generateCorrelationId());
        
        return problemDetail;
    }

    private String generateCorrelationId() {
        // Na prática, isso viria do MDC ou do header da request
        return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
