package com.crud.restapi.exceptions;

import com.crud.restapi.io.ErrorObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
/**
 * JPA repository for Expense resource
 * */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ErrorObject handleResourceNotFoundException(ResourceNotFoundException exeption, WebRequest request) {
        log.error("Throwing the ResourceNotFoundExeption from GlobalExeptionsHendler {}" + exeption.getMessage());
        return ErrorObject.builder()
        .errorCode("DATA_NOT_FOUND")
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(exeption.getMessage())
        .timestamp(new Date())
        .build();
    }
}
