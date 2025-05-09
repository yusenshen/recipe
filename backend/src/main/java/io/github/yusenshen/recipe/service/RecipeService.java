package io.github.yusenshen.recipe.service;

import io.github.yusenshen.recipe.dto.*;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
    RecipeResponse createRecipe(RecipeRequest request);
    RecipeResponse updateRecipe(Long id, RecipeRequest request);
    void deleteRecipe(Long id);
    List<RecipeResponse> getAllRecipes();
    RecipeResponse getRecipeById(Long id);
    String uploadImage(MultipartFile file);
}