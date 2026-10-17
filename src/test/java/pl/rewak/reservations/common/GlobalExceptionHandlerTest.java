package pl.rewak.reservations.common;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void apiExceptionBecomesProblemDetailWithCode() {
        ApiException ex = new ApiException(HttpStatus.CONFLICT, "RESERVATION_CONFLICT",
                "Termin jest już zajęty", "Zasób Sala 101 jest zarezerwowany w wybranym przedziale.");

        ProblemDetail problem = handler.handleApiException(ex);

        assertThat(problem.getStatus()).isEqualTo(409);
        assertThat(problem.getTitle()).isEqualTo("Termin jest już zajęty");
        assertThat(problem.getDetail()).isEqualTo("Zasób Sala 101 jest zarezerwowany w wybranym przedziale.");
        assertThat(problem.getType()).isEqualTo(URI.create("https://reservations.local/errors/reservation-conflict"));
        assertThat(problem.getProperties()).containsEntry("code", "RESERVATION_CONFLICT");
    }

    @Test
    void validationErrorReturns400WithFieldList() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "title", "must not be blank"));
        bindingResult.addError(new FieldError("request", "endAt", "must be in the future"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleEndpoint", Object.class), 0);

        ProblemDetail problem = handler.handleValidation(
                new MethodArgumentNotValidException(parameter, bindingResult));

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getProperties())
                .containsEntry("code", "VALIDATION_ERROR")
                .containsEntry("errors", List.of(
                        Map.of("field", "title", "message", "must not be blank"),
                        Map.of("field", "endAt", "message", "must be in the future")));
    }

    @SuppressWarnings("unused")
    private void sampleEndpoint(Object request) {
    }
}
