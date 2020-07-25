package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    void deleteAllByRecipeId(long id);

}
