package com.example.demo.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus status;
        String message;

        if (ex instanceof UsuarioNotFoundException || ex instanceof ProductoNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if ( ex instanceof DuplicateProductException) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (ex.getMessage() != null) {
            message = ex.getMessage();
        } else {
            message = "An error occurred.";
        }

        return new ResponseEntity<>(message, status);
    }
}
