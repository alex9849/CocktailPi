package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
