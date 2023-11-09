package com.example.demo.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateProductException extends RuntimeException{
    private static final long serialVersionUID = 702816345912435678L;
    public ResponseEntity<Object> DuplicateProductException(Long id) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("The product with this id"+ id + "already exists");
    }
}
