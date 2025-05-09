package io.github.yusenshen.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle recipe not found
    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecipeNotFound(RecipeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of("RecipeNotFound", ex.getMessage()));
    }

    // handle duplicate recipe
    @ExceptionHandler(DuplicateRecipeException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateRecipeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of("DuplicateRecipe", ex.getMessage()));
    }

    // handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(
                ErrorResponse.of("ValidationError", message));
    }

    // handle image upload errors
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUpload(ImageUploadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("ImageUploadError", ex.getMessage()));
    }

    // handle any other errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of("InternalServerError", ex.getMessage()));
    }
}
