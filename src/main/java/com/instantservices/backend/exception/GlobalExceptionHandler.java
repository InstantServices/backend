package com.instantservices.backend.exception;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//  This will handle ALL exceptions globally
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        ex.printStackTrace();
        return ResponseEntity.badRequest().body(
                new ErrorResponse("ERROR", ex.getMessage())
        );
    }

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(
                new ErrorResponse("INTERNAL_ERROR", "Something went wrong")
        );
    }
}