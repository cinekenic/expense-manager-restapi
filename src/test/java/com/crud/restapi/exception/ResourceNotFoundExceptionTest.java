package com.crud.restapi.exception;

import com.crud.restapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldStoreErrorMessage() {
        // given
        String message = "Resource not found";

        // when
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
