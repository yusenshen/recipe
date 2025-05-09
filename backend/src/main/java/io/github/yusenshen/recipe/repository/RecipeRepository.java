package io.github.yusenshen.recipe.repository;

import io.github.yusenshen.recipe.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByDifficulty(Recipe.Difficulty difficulty);
    boolean existsByTitleAndCreatorName(String title, String creatorName);
}