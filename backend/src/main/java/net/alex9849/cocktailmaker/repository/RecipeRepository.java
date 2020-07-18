package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {


    List<Recipe> findAllByOwnerId(long id);
}
