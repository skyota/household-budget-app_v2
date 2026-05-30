package com.kakeibo.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakeibo.application.exception.CategoryNameAlreadyExistsException;
import com.kakeibo.application.exception.CategoryNotFoundException;
import com.kakeibo.application.exception.ExpenseNotFoundException;
import com.kakeibo.application.exception.InvalidCredentialsException;
import com.kakeibo.application.exception.InvalidSessionException;
import com.kakeibo.application.exception.UsernameAlreadyExistsException;
import com.kakeibo.domain.exception.DomainValidationException;
import com.kakeibo.presentation.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 409 Conflict
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(CategoryNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(CategoryNameAlreadyExistsException e) {
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

    // 404 Not Found
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(CategoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ExpenseNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handle(DomainValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }

    // @Validでバリデーションエラーが発生したときに呼ばれる
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        // 最初のエラーメッセージを取得して返す
        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("リクエストの値が不正です。");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message));
    }
}
