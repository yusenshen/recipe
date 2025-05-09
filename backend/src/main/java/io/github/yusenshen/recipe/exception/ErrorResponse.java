package io.github.yusenshen.recipe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, LocalDateTime.now());
    }
}
