package com.kakeibo.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakeibo.application.exception.InvalidCredentialsException;
import com.kakeibo.application.exception.InvalidSessionException;
import com.kakeibo.application.exception.UsernameAlreadyExistsException;
import com.kakeibo.presentation.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 409 Conflict
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }

    // 401 Unauthorized
    @ExceptionHandler({
        InvalidCredentialsException.class,
        InvalidSessionException.class,
        UnauthorizedException.class
    })
    public ResponseEntity<ErrorResponse> handle(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(e.getMessage()));
    }
}
