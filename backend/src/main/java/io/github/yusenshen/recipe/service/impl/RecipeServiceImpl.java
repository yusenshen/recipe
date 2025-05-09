package io.github.yusenshen.recipe.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.yusenshen.recipe.dto.*;
import io.github.yusenshen.recipe.exception.*;
import io.github.yusenshen.recipe.model.*;
import io.github.yusenshen.recipe.repository.RecipeRepository;
import io.github.yusenshen.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository repository;

    // create recipe
    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {

        boolean exists = repository.existsByTitleAndCreatorName(
                request.getTitle(), request.getCreatorName());

        if (exists) {
            throw new DuplicateRecipeException(request.getTitle());
        }

        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .difficulty(request.getDifficulty())
                .instruction(request.getInstruction())
                .imageUrl(request.getImageUrl())
                .creatorName(request.getCreatorName())
                .build();
        recipe.setCreatedDate(LocalDateTime.now());
        List<Ingredient> ingredients = request.getIngredients().stream()
                .map(name -> Ingredient.builder()
                        .name(name)
                        .recipe(recipe)
                        .build())
                .collect(Collectors.toList());
        recipe.setIngredients(ingredients);

        return toResponse(repository.save(recipe));
    }

    // get all recipes
    @Override
    public List<RecipeResponse> getAllRecipes() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // get one recipe
    @Override
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = repository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
        return toResponse(recipe);
    }

    // update recipe
    @Override
    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) {
        return repository.findById(id)
                .map(recipe -> {
                    // if imageUrl is changed, delete old image file
                    String oldImageUrl = recipe.getImageUrl();
                    String newImageUrl = request.getImageUrl();

                    if (oldImageUrl != null && !oldImageUrl.isEmpty()
                            && newImageUrl != null && !newImageUrl.equals(oldImageUrl)) {
                        try {
                            String filename = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);
                            Path imagePath = Paths.get("uploads").resolve(filename);
                            Files.deleteIfExists(imagePath);
                        } catch (IOException e) {
                            System.err.println("Failed to delete old image during update: " + e.getMessage());
                        }
                    }

                    recipe.setTitle(request.getTitle());
                    recipe.setDifficulty(request.getDifficulty());
                    recipe.setInstruction(request.getInstruction());
                    recipe.setImageUrl(newImageUrl);
                    recipe.setCreatorName(request.getCreatorName());
                    recipe.getIngredients().clear();
                    List<Ingredient> ingredients = request.getIngredients().stream()
                            .map(name -> Ingredient.builder()
                                    .name(name)
                                    .recipe(recipe)
                                    .build())
                            .collect(Collectors.toList());
                    recipe.getIngredients().addAll(ingredients);

                    return toResponse(repository.save(recipe));
                })
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    // delete recipe
    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = repository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));

        // delete image file if exists
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            try {
                String filename = recipe.getImageUrl().substring(recipe.getImageUrl().lastIndexOf("/") + 1);
                Path imagePath = Paths.get("uploads").resolve(filename);
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + e.getMessage());
            }
        }
        repository.delete(recipe);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "http://localhost:8080/" + filename;
        } catch (IOException e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

    // private method to convert entity to DTO
    private RecipeResponse toResponse(Recipe recipe) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setTitle(recipe.getTitle());
        response.setDifficulty(recipe.getDifficulty());
        response.setInstruction(recipe.getInstruction());
        response.setImageUrl(recipe.getImageUrl());
        response.setCreatorName(recipe.getCreatorName());
        response.setCreatedDate(recipe.getCreatedDate());
        response.setIngredients(
                recipe.getIngredients().stream()
                        .map(Ingredient::getName)
                        .collect(Collectors.toList()));
        return response;
    }
}
