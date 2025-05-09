package io.github.yusenshen.recipe.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ingredients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // many ingredients belong to one recipe
    @JoinColumn(name = "recipe_id") // foreign key field
    private Recipe recipe;

    @Column(nullable = false, length = 200)
    private String name;
}
