package io.github.yusenshen.recipe.dto;

import io.github.yusenshen.recipe.model.Recipe;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecipeResponse {
    private Long id;
    private String title;
    private Recipe.Difficulty difficulty;
    private String instruction;
    private String imageUrl;
    private String creatorName;
    private List<String> ingredients;
    private LocalDateTime createdDate;
}