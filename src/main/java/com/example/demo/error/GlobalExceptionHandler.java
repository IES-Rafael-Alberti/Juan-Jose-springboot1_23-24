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
            message = "The item was not found in the database";
            return new ResponseEntity<>(message, status);
        } else if ( ex instanceof DuplicateProductException) {
            status = HttpStatus.CONFLICT;
            message = "The item already exists in the database";
            return new ResponseEntity<>(message, status);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An error not handled has occurred ";
            return new ResponseEntity<>(message, status);
        }



    }
}
