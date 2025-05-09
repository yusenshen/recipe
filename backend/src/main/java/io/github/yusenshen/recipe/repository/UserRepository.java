package io.github.yusenshen.recipe.repository;

import io.github.yusenshen.recipe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByUsername(String username);
    User findByUsername(String username);
}
