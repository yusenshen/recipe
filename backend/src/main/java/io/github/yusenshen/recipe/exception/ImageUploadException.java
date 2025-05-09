package io.github.yusenshen.recipe.exception;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String reason) {
        super("Image upload failed: " + reason);
    }
}
