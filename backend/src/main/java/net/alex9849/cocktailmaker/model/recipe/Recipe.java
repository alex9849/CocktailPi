package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.Glass;
import net.alex9849.cocktailmaker.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.service.CategoryService;
import net.alex9849.cocktailmaker.service.GlassService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.SpringUtility;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Recipe {
    private long id;
    private String name;
    private boolean hasImage;
    private User owner;
    private Long ownerId;
    private String description;
    private Date lastUpdate;

    private Long defaultGlassId;
    private Glass defaultGlass;
    private List<ProductionStep> productionSteps;
    private List<Category> categories;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    //
    // Lazy loading methods
    //

    public Long getOwnerId() {
        return ownerId;
    }

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
