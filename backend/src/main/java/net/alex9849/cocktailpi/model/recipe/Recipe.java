package net.alex9849.cocktailpi.model.recipe;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.service.CategoryService;
import net.alex9849.cocktailpi.service.GlassService;
import net.alex9849.cocktailpi.service.RecipeService;
import net.alex9849.cocktailpi.service.UserService;
import net.alex9849.cocktailpi.utils.SpringUtility;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Recipe {
    @Setter @Getter
    private long id;
    @Setter @Getter
    private String name;
    @Setter @Getter
    private boolean hasImage;
    private User owner;
    @Getter
    private Long ownerId;
    @Setter @Getter
    private String description;
    @Setter @Getter
    private Date lastUpdate;

    private Long defaultGlassId;
    private Glass defaultGlass;
    private List<ProductionStep> productionSteps;
    private List<Category> categories;

    public int alcoholPercentageMin() {
        int totalAmountMl = 0;
        double totalAlcoholMinMl = 0;
        for(ProductionStep productionStep : productionSteps) {
            if (productionStep instanceof AddIngredientsProductionStep aiPs) {
                for (ProductionStepIngredient psi : aiPs.getStepIngredients()) {
                    totalAmountMl += psi.getAmount();
                    Ingredient ingredient = psi.getIngredient();
                    if(ingredient instanceof AddableIngredient addableIngredient) {
                        totalAlcoholMinMl += (addableIngredient.getAlcoholContent() * psi.getAmount()) / 100d;
                    } else if (ingredient instanceof IngredientGroup ingredientGroup) {
                        Integer minGroup = ingredientGroup.getMinAlcoholContent();
                        if (minGroup == null) {
                            continue;
                        }
                        totalAlcoholMinMl += (minGroup * psi.getAmount()) / 100d;
                    }
                }
            }
        }
        if (totalAmountMl == 0) {
            return 0;
        }
        return (int) (totalAlcoholMinMl * 100) / totalAmountMl;
    }

    public int alcoholPercentageMax() {
        int totalAmountMl = 0;
        double totalAlcoholMaxMl = 0;
        for(ProductionStep productionStep : productionSteps) {
            if (productionStep instanceof AddIngredientsProductionStep aiPs) {
                for (ProductionStepIngredient psi : aiPs.getStepIngredients()) {
                    totalAmountMl += psi.getAmount();
                    Ingredient ingredient = psi.getIngredient();
                    if(ingredient instanceof AddableIngredient addableIngredient) {
                        totalAlcoholMaxMl += (addableIngredient.getAlcoholContent() * psi.getAmount()) / 100d;
                    } else if (ingredient instanceof IngredientGroup ingredientGroup) {
                        Integer maxGroup = ingredientGroup.getMaxAlcoholContent();
                        if (maxGroup == null) {
                            continue;
                        }
                        totalAlcoholMaxMl += (maxGroup * psi.getAmount()) / 100d;
                    }
                }
            }
        }
        if (totalAmountMl == 0) {
            return 0;
        }
        return (int) (totalAlcoholMaxMl * 100) / totalAmountMl;
    }

    //
    // Lazy loading methods
    //

    public void setOwnerId(Long ownerId) {
        if(!Objects.equals(this.ownerId, ownerId)) {
            this.owner = null;
        }
        this.ownerId = ownerId;
    }

    public boolean isBoostable() {
        List<ProductionStepIngredient> productionStepIngredients = this.getProductionSteps().stream()
                .filter(x -> x instanceof AddIngredientsProductionStep)
                .map(x -> (AddIngredientsProductionStep) x)
                .flatMap(x -> x.getStepIngredients().stream())
                .toList();

        return productionStepIngredients.stream().anyMatch(ProductionStepIngredient::isBoostable)
                && ! productionStepIngredients.stream().allMatch(ProductionStepIngredient::isBoostable);
    }

    public Glass getDefaultGlass() {
        if(defaultGlassId == null) {
            return null;
        }
        if(defaultGlass == null) {
            GlassService gService = SpringUtility.getBean(GlassService.class);
            defaultGlass = gService.getById(defaultGlassId);
        }
        if(defaultGlass == null) {
            defaultGlassId = null;
        }
        return defaultGlass;
    }

    public void setDefaultGlass(Glass defaultGlass) {
        this.defaultGlassId = (defaultGlass != null)? defaultGlass.getId() : null;
        this.defaultGlass = defaultGlass;
    }

    public long getDefaultGlassId() {
        return defaultGlassId;
    }

    public void setDefaultGlassId(Long defaultGlassId) {
        if(!Objects.equals(this.defaultGlassId, defaultGlassId)) {
            this.defaultGlass = null;
        }
        this.defaultGlassId = defaultGlassId;
    }

    public User getOwner() {
        if(ownerId == null) {
            return null;
        }
        if(owner == null) {
            UserService uService = SpringUtility.getBean(UserService.class);
            owner = uService.getUser(ownerId);
        }
        if(owner == null) {
            ownerId = null;
        }
        return owner;
    }

    public void setOwner(User owner) {
        this.ownerId = (owner != null)? owner.getId() : null;
        this.owner = owner;
    }

    public List<ProductionStep> getProductionSteps() {
        if(this.productionSteps == null) {
            RecipeService rService = SpringUtility.getBean(RecipeService.class);
            this.productionSteps = rService.getProductionStepsByRecipeId(this.id);
        }
        return this.productionSteps;
    }

    public String getNormalName() {
        return SpringUtility.normalize(getName());
    }

    public void setProductionSteps(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<Category> getCategories() {
        if(this.categories == null) {
            CategoryService cService = SpringUtility.getBean(CategoryService.class);
            this.categories = cService.getByRecipeId(this.id);
        }
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
