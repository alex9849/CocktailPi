package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.repository.CategoryRepository;
import net.alex9849.cocktailmaker.repository.ProductionStepRepository;
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
    private long defaultAmountToFill;
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

    public long getDefaultAmountToFill() {
        return defaultAmountToFill;
    }

    public void setDefaultAmountToFill(long defaultAmountToFill) {
        this.defaultAmountToFill = defaultAmountToFill;
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
                .collect(Collectors.toList());

        return productionStepIngredients.stream().anyMatch(ProductionStepIngredient::isBoostable)
                && ! productionStepIngredients.stream().allMatch(ProductionStepIngredient::isBoostable);
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
            ProductionStepRepository psRepository = SpringUtility.getBean(ProductionStepRepository.class);
            this.productionSteps = psRepository.loadByRecipeId(this.id);
        }
        return this.productionSteps;
    }

    public void setProductionSteps(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<Category> getCategories() {
        if(this.categories == null) {
            CategoryRepository cRepository = SpringUtility.getBean(CategoryRepository.class);
            this.categories = cRepository.findByRecipeId(this.id);
        }
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
