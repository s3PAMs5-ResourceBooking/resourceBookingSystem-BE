package pl.rewak.reservations.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Bazowy wyjątek domenowy. {@link GlobalExceptionHandler} zamienia go na
 * {@code ProblemDetail} z polem {@code code}.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String title;

    public ApiException(HttpStatus status, String code, String title, String detail) {
        super(detail);
        this.status = status;
        this.code = code;
        this.title = title;
    }
}
