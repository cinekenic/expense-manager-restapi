package com.crud.restapi.exception;

import com.crud.restapi.exceptions.GlobalExceptionHandler;
import com.crud.restapi.exceptions.ItemExistsException;
import com.crud.restapi.io.ErrorObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleItemExistsException() {
        // given
        ItemExistsException exception = new ItemExistsException("Item already exists");
        WebRequest request = mock(WebRequest.class);

        // when
        ErrorObject response = handler.handleItemExistsException(exception, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getErrorCode()).isEqualTo("DATA_EXISTS");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getMessage()).isEqualTo("Item already exists");
        assertThat(response.getTimestamp()).isBeforeOrEqualTo(new Date());
    }

    @Test
    void shouldHandleGeneralException() {
        // given
        Exception exception = new Exception("Something went wrong");
        WebRequest request = mock(WebRequest.class);

        // when
        ErrorObject response = handler.handleGeneralException(exception, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getErrorCode()).isEqualTo("UNEXPECTED_ERROR");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getMessage()).isEqualTo("Something went wrong");
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("object", "field", "Field is required"))
        );

        Method method = SampleController.class.getMethod("sampleMethod", String.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        WebRequest request = mock(WebRequest.class);

        // when
        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                exception, null, HttpStatus.BAD_REQUEST, request
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("statusCode", 400);
        assertThat(body).containsEntry("errorCode", "VALIDATION_FAILED");
        assertThat(body).containsKey("message");
    }

    // potrzebna do symulacji refleksji w wyjątku walidacyjnym
    static class SampleController {
        public void sampleMethod(String field) {}
    }
}
