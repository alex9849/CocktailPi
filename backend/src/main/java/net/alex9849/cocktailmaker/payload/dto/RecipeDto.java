package net.alex9849.cocktailmaker.payload.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ProductionStepDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @Size(min = 1, max = 30) String getName(); }
    private interface Description { @NotNull @Size(min = 1, max = 3000) String getDescription(); }
    private interface ProductionSteps { @NotNull @NotEmpty List<ProductionStepDto> getProductionSteps(); }
    private interface Categories { @NotNull Set<CategoryDto> getCategories(); }
    private interface DefaultAmountToFill { @NotNull @Min(50) long getDefaultAmountToFill(); }

    private interface OwnerName { String getOwnerName(); }
    private interface HasImage { boolean isHasImage(); }
    private interface Ingredients { Set<IngredientDto> getIngredients(); };
    private interface LastUpdate { Date getLastUpdate(); }
    private interface OwnerId { long getOwnerId(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Data
        public static class Create implements Name, OwnerId, Description, ProductionSteps, Categories, DefaultAmountToFill {
            String name;
            long ownerId;
            String description;
            List<ProductionStepDto> productionSteps;
            Set<CategoryDto> categories;
            long defaultAmountToFill;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Data
        public static class Detailed implements Name, OwnerId, Description, ProductionSteps, Categories,
                HasImage, DefaultAmountToFill, LastUpdate {
            final long id;
            String name;
            long ownerId;
            String description;
            List<ProductionStepDto> productionSteps;
            Set<CategoryDto> categories;
            boolean hasImage;
            long defaultAmountToFill;
            Date lastUpdate;

            public static Detailed fromEntity(Recipe recipe) {
                Detailed dto = new Detailed(recipe.getId());
                BeanUtils.copyProperties(recipe, dto);
                dto.productionSteps = recipe.getProductionSteps().stream()
                        .map(ProductionStepDto::toDto).collect(Collectors.toList());
                dto.categories = recipe.getCategories().stream().map(CategoryDto::new)
                        .collect(Collectors.toSet());
                return dto;
            }
        }

        @Data
        public static class SearchResult implements Id, Name, OwnerName, Description, HasImage, Ingredients {
            final long id;
            String name;
            String ownerName;
            String description;
            boolean hasImage;
            Set<IngredientDto> ingredients;
        }
    }
}
