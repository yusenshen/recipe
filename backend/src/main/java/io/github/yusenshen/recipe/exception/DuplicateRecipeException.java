package io.github.yusenshen.recipe.exception;

public class DuplicateRecipeException extends RuntimeException {
    public DuplicateRecipeException(String title) {
        super("This user has already uploaded a recipe titled \"" + title + "\", please do not add it again.");
    }
}
