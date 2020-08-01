package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, JpaSpecificationExecutor<Ingredient> {

    Optional<Ingredient> findByName(String name);
}
