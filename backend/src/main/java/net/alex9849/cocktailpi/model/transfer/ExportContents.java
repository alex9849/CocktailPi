package net.alex9849.cocktailpi.model.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class ExportContents {
    private long importId;
    private List<RecipeDto.Response.Detailed> recipes;
    private List<CategoryDto.Duplex.Detailed> categories;
    private List<GlassDto.Duplex.Detailed> glasses;
    private List<IngredientDto.Response.Detailed> ingredients;
    private List<CollectionDto.Response.Detailed> collections;
    private Map<Long, List<Long>> collectionRecipes;
}
