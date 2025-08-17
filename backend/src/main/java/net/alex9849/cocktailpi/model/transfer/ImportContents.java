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

@Getter @Setter @NoArgsConstructor
public class ImportContents {
    private long importId;
    private List<RecipeDto.Response.Detailed> recipeDtos;
    private List<CategoryDto.Duplex.Detailed> categoryDtos;
    private List<GlassDto.Duplex.Detailed> glassDtos;
    private List<IngredientDto.Response.Detailed> ingredientDtos;
    private List<CollectionDto.Response.Detailed> collectionDtos;
}
