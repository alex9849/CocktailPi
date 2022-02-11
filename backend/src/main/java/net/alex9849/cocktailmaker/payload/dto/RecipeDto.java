package net.alex9849.cocktailmaker.payload.dto;

import lombok.Data;
import lombok.Value;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ProductionStepDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

public enum RecipeDto {;
    public enum Request {;
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
    public enum Response {;
        @Value public static class Detailed implements Name, OwnerId, Description, ProductionSteps, Categories, DefaultAmountToFill {
            long id;
            String name;
            long ownerId;
            String description;
            List<ProductionStepDto> productionSteps;
            Set<CategoryDto> categories;
            long defaultAmountToFill;
        }

        @Value public static class SearchResult implements Id, Name, OwnerName, Description, HasImage, Ingredients {
            long id;
            String name;
            String ownerName;
            String description;
            boolean hasImage;
            Set<IngredientDto> ingredients;
        }
    }
    private interface Id { long getId(); }
    private interface Name { @NotNull @Size(min = 1, max = 30) String getName(); }
    private interface OwnerName { String getOwnerName(); }
    private interface OwnerId { long getOwnerId(); }
    private interface Description { @NotNull @Size(min = 0, max = 3000) String getDescription(); }
        private interface ProductionSteps { @NotNull List<ProductionStepDto> getProductionSteps(); };
    private interface HasImage { boolean isHasImage(); }
private interface Ingredients { Set<IngredientDto> getIngredients(); }
    private interface Categories { @NotNull Set<CategoryDto> getCategories(); }

    private interface DefaultAmountToFill { @NotNull long getDefaultAmountToFill(); }

    private interface LastUpdate { Date getLastUpdate(); }

}
