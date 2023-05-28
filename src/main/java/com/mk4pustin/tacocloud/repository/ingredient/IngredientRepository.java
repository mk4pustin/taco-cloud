package com.mk4pustin.tacocloud.repository.ingredient;

import com.mk4pustin.tacocloud.data.ingredient.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    List<Ingredient> findAll();

    Optional<Ingredient> findById(String id);

    Ingredient save(Ingredient ingredient);
}
