package pl.rewak.reservations.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Wszystkie błędy API w formacie Problem Details (RFC 9457) z dodatkowym polem {@code code}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    static final String ERROR_TYPE_BASE = "https://reservations.local/errors/";
    static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex) {
        return problem(ex.getStatus(), ex.getCode(), ex.getTitle(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, VALIDATION_ERROR,
                "Niepoprawne dane wejściowe", "Żądanie zawiera niepoprawne pola.");
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toFieldError)
                .toList();
        problem.setProperty("errors", errors);
        return problem;
    }

    static ProblemDetail problem(HttpStatus status, String code, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(ERROR_TYPE_BASE + code.toLowerCase(Locale.ROOT).replace('_', '-')));
        problem.setTitle(title);
        problem.setProperty("code", code);
        return problem;
    }

    private static Map<String, String> toFieldError(FieldError error) {
        return Map.of(
                "field", error.getField(),
                "message", String.valueOf(error.getDefaultMessage()));
    }
}
