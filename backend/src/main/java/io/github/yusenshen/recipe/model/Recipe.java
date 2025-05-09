package io.github.yusenshen.recipe.model; // package name matches folder

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "recipes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    public enum Difficulty {EASY,MEDIUM,HARD}

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Lob
    private String instruction;
    
    @Column(length = 200)
    private String imageUrl;

    @Column(nullable = false, length = 200)
    private String creatorName;

    @OneToMany(
        mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Ingredient> ingredients = new ArrayList<>();
    
    private LocalDateTime createdDate = LocalDateTime.now(); // auto set create time
}
