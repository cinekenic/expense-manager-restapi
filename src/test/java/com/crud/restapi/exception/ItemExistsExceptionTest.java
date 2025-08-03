package com.crud.restapi.exception;


import com.crud.restapi.exceptions.ItemExistsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemExistsExceptionTest {

    @Test
    void shouldStoreMessage() {
        // given
        String message = "Email already exists";

        // when
        ItemExistsException exception = new ItemExistsException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
