package io.github.yusenshen.recipe.dto;

import io.github.yusenshen.recipe.model.Recipe;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class RecipeRequest {

    @NotBlank(message = "Recipe title cannot be empty")
    private String title;

    @NotNull(message = "Difficulty cannot be empty")
    private Recipe.Difficulty difficulty;

    @NotBlank(message = "Instruction cannot be empty")
    private String instruction;

    private String imageUrl;

    @NotBlank(message = "Creator name cannot be empty")
    private String creatorName;

    @NotEmpty(message = "At least one ingredient is required")
    private List<@NotBlank String> ingredients;
 
}
