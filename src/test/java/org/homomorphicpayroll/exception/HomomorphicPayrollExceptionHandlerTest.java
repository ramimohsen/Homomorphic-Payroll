package org.homomorphicpayroll.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.homomorphicpayroll.exception.response.ErrorDetails;
import org.homomorphicpayroll.exception.response.ValidationFailedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomomorphicPayrollExceptionHandlerTest {

    @InjectMocks
    private HomomorphicPayrollExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstraintViolationExceptionHandling() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("field");
        when(violation.getMessage()).thenReturn("must not be null");

        ResponseEntity<Object> response = exceptionHandler.onConstraintValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationFailedResponse body = (ValidationFailedResponse) response.getBody();
        assert body != null;
        assertEquals(1, body.getViolations().size());
        assertEquals("field", body.getViolations().getFirst().fieldName());
        assertEquals("must not be null", body.getViolations().getFirst().message());
    }

    @Test
    void testRuntimeExceptionHandling() {
        RuntimeException exception = new RuntimeException("Runtime exception occurred");

        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        ResponseEntity<Object> response = exceptionHandler.runtimeExceptionHandling(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetails body = (ErrorDetails) response.getBody();
        assert body != null;
        assertEquals("Runtime exception occurred", body.getMessage());
        assertEquals("uri=/test", body.getDetails());
    }

    @Test
    void testGlobalExceptionHandling() {
        Exception exception = new Exception("Global exception occurred");

        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        ResponseEntity<Object> response = exceptionHandler.globalExceptionHandling(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorDetails body = (ErrorDetails) response.getBody();
        assert body != null;
        assertEquals("Global exception occurred", body.getMessage());
        assertEquals("uri=/test", body.getDetails());
    }
}