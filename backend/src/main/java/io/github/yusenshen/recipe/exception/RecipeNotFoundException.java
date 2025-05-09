package io.github.yusenshen.recipe.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(Long id) {
        super("Recipe with ID " + id + " not found");
    }
}
