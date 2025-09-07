package net.alex9849.cocktailpi.payload.dto.recipe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.recipe.IngredientRecipe;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepDto;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @Size(min = 1, max = 50) String getName(); }
    private interface NormalName { String getNormalName(); }
    private interface Description { @Size(max = 3000) String getDescription(); }
    private interface ProductionStepsCreated { @NotNull @NotEmpty List<ProductionStepDto.Request.Create> getProductionSteps(); }
    private interface ProductionStepsDetailed { @NotNull @NotEmpty List<ProductionStepDto.Response.Detailed> getProductionSteps(); }
    private interface Categories { @NotNull Set<CategoryDto.Duplex.Detailed> getCategories(); }
    private interface CategoryIds { @NotNull Set<Long> getCategoryIds(); }
    private interface DefaultGlass { GlassDto.Duplex.Detailed getDefaultGlass(); }
    private interface MinAlcoholContent { int getMinAlcoholContent(); }
    private interface MaxAlcoholContent { int getMaxAlcoholContent(); }

    private interface DefaultGlassId { Long getDefaultGlassId(); }

    private interface OwnerName { String getOwnerName(); }
    private interface Boostable { boolean isBoostable(); }
    private interface HasImage { boolean isHasImage(); }
    private interface UniqueIngredients { Set<IngredientDto.Response.Reduced> getIngredients(); };
    private interface LastUpdate { Date getLastUpdate(); }
    private interface OwnerId { long getOwnerId(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create implements Name, OwnerId, Description, ProductionStepsCreated, CategoryIds, DefaultGlassId {
            String name;
            long ownerId;
            String description;
            List<ProductionStepDto.Request.Create> productionSteps;
            Set<Long> categoryIds;
            Long defaultGlassId;

            public Create() {}

            public Create(Response.Detailed detailed) {
                BeanUtils.copyProperties(detailed, this);
                this.productionSteps = detailed.getProductionSteps().stream()
                        .map(ProductionStepDto.Request.Create::fromDetailedDto)
                        .collect(Collectors.toList());
                this.categoryIds = detailed.getCategories()
                        .stream().map(CategoryDto.Duplex.Detailed::getId)
                        .collect(Collectors.toSet());
                if(detailed.getDefaultGlass() != null) {
                    this.defaultGlassId = detailed.getDefaultGlass().getId();
                }
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        public static class Detailed implements Name, NormalName, OwnerId, Description, ProductionStepsDetailed, Categories,
                HasImage, DefaultGlass, LastUpdate, Boostable, MinAlcoholContent, MaxAlcoholContent, OwnerName {
            long id;
            String name;
            String normalName;
            long ownerId;
            String ownerName;
            boolean boostable;
            String description;
            List<ProductionStepDto.Response.Detailed> productionSteps;
            Set<CategoryDto.Duplex.Detailed> categories;
            boolean hasImage;
            GlassDto.Duplex.Detailed defaultGlass;
            Date lastUpdate;
            int minAlcoholContent;
            int maxAlcoholContent;

            public Detailed() {}

            public Detailed(Recipe recipe) {
                this.id = recipe.getId();
                this.boostable = recipe.isBoostable();
                this.normalName = recipe.getNormalName();
                BeanUtils.copyProperties(recipe, this);
                this.productionSteps = recipe.getProductionSteps().stream()
                        .map(ProductionStepDto.Response.Detailed::toDto).collect(Collectors.toList());
                this.categories = recipe.getCategories().stream().map(CategoryDto.Duplex.Detailed::new)
                        .collect(Collectors.toSet());
                if(recipe.getDefaultGlass() != null) {
                    this.defaultGlass = new GlassDto.Duplex.Detailed(recipe.getDefaultGlass());
                }
                this.ownerName = recipe.getOwner().getUsername();
                this.minAlcoholContent = recipe.alcoholPercentageMin();
                this.maxAlcoholContent = recipe.alcoholPercentageMax();
            }

            public static RecipeDto.Response.Detailed toDto(Recipe recipe) {
                if(recipe instanceof IngredientRecipe ingredientRecipe) {
                    return new IngredientRecipeDto.Response.Detailed(ingredientRecipe);
                } else {
                    return new Detailed(recipe);
                }
            }

            public String getType() {
                return "recipe";
            }
        }

        @Getter @Setter @EqualsAndHashCode
        public static class SearchResult implements Id, Name, OwnerName, Description, HasImage, UniqueIngredients,
                LastUpdate, MinAlcoholContent, MaxAlcoholContent {
            long id;
            String name;
            String ownerName;
            String description;
            boolean hasImage;
            Set<IngredientDto.Response.Reduced> ingredients;
            Date lastUpdate;
            int minAlcoholContent;
            int maxAlcoholContent;

            public SearchResult(Recipe recipe) {
                this.id = recipe.getId();
                BeanUtils.copyProperties(recipe, this);
                this.setOwnerName(recipe.getOwner().getUsername());
                this.ingredients = recipe.getProductionSteps().stream()
                        .filter(x -> x instanceof AddIngredientsProductionStep)
                        .flatMap(x -> ((AddIngredientsProductionStep) x).getStepIngredients().stream())
                        .map(ProductionStepIngredient::getIngredient)
                        .map(IngredientDto.Response.Reduced::toDto)
                        .collect(Collectors.toSet());
                this.minAlcoholContent = recipe.alcoholPercentageMin();
                this.maxAlcoholContent = recipe.alcoholPercentageMax();
            }

            public static RecipeDto.Response.SearchResult toDto(Recipe recipe) {
                if(recipe instanceof IngredientRecipe ingredientRecipe) {
                    return new IngredientRecipeDto.Response.SearchResult(ingredientRecipe);
                } else {
                    return new SearchResult(recipe);
                }
            }

            public String getType() {
                return "recipe";
            }
        }
    }
}
