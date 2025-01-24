package org.example.doctorai.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityAlreadyExistException.class)
    public String handleException(EntityAlreadyExistException e) {
        return e.getMessage();
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public String handleException(DuplicateEmailException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public String handleException(JwtException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(ChatNotFoundException.class)
    public String handleException(ChatNotFoundException e) {return e.getMessage();}

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(GigaChataException.class)
    public String handleException(GigaChataException e) {return e.getMessage();}
}
