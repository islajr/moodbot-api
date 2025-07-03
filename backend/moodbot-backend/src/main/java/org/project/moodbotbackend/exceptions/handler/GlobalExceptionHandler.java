package org.project.moodbotbackend.exceptions.handler;

import org.project.moodbotbackend.exceptions.ErrorResponse;
import org.project.moodbotbackend.exceptions.auth.AuthException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(e.getStatusCode())).body(new ErrorResponse(e.getMessage()));
    }

}
