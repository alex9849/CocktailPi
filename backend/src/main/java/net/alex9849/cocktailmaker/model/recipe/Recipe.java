package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.user.User;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    private boolean inPublic;

    @NotNull
    @ManyToOne()
    private User owner;

    private Long ownerId;

    @Lob() @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @NotNull
    @Size(min = 0, max = 3000)
    private String description;

    @Temporal(value = TemporalType.TIMESTAMP)
    @NotNull
    private Date lastUpdate;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RecipeIngredient> recipeIngredients;

    @ManyToMany()
    @JoinTable(name = "recipe_categories",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "categories_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Category> categories;

    @Formula("(select count(*) = count(uoi.ingredient_id)\n" +
            "        from recipe_ingredients ri\n" +
            "                 join ingredients i on ri.ingredient_id = i.id\n" +
            "                 left join user_owned_ingredients uoi on i.id = uoi.ingredient_id\n" +
            "        where ri.recipe_id = id)")
    private boolean ingredientsInBar;

    @PrePersist
    protected void onCreate() {
        lastUpdate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInPublic() {
        return inPublic;
    }

    public void setInPublic(boolean inPublic) {
        this.inPublic = inPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public boolean isIngredientsInBar() {
        return ingredientsInBar;
    }

    public static class RecipeFilterCurrentlyMakeable implements Specification<Recipe> {

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

            Subquery<Recipe> subquery = query.subquery(Recipe.class);
            /* This would be better, because we wouldn't need an extra attribute to join
               on in the Ingredient.class. The Problem is, that this solution would need a right-join
               and right-joins are not supported by Spring-JPA / Hibernate

            Root<Pump> pump = subquery.from(Pump.class);
            Join<Pump, Ingredient> ingredient = pump.join("currentIngredient");
            Join<Ingredient, RecipeIngredient> recipeIngredient = ingredient.join("recipeIngredients", JoinType.RIGHT);
            Join<RecipeIngredient, Recipe> recipe = recipeIngredient.join("recipe");
            */

            Root<Recipe> recipe = subquery.from(Recipe.class);
            Join<Recipe, RecipeIngredient> recipeIngredient = recipe.join("recipeIngredients");
            Join<RecipeIngredient, Ingredient> ingredient = recipeIngredient.join("ingredient");
            Join<Ingredient, Pump> pump = ingredient.join("pumps", JoinType.LEFT);

            subquery.select(recipe)
                    .groupBy(recipe.get("id"))
                    .having(criteriaBuilder.equal(criteriaBuilder.count(pump.get("id")),
                            criteriaBuilder.count(recipeIngredient.get("id").get("IngredientId"))));
            return criteriaBuilder.in(root).value(subquery);
        }

    }

    public static class RecipeFilterInBar implements Specification<Recipe> {

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.equal(root.get("ingredientsInBar"), true);
        }
    }

    public static class RecipeFilterContainsIngredients implements Specification<Recipe> {
        private final Long[] containsIngredients;

        public RecipeFilterContainsIngredients(Long[] containsIngredients) {
            this.containsIngredients = containsIngredients;
        }

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Subquery<Recipe> subquery = query.subquery(Recipe.class);
            Root<Recipe> recipe = subquery.from(Recipe.class);
            Join<Recipe, RecipeIngredient> recipeIngredient = recipe.join("recipeIngredients");

            subquery.select(recipe)
                    .where(recipeIngredient.get("ingredient").in(containsIngredients))
                    .groupBy(recipe.get("id"))
                    .having(criteriaBuilder.equal(criteriaBuilder.countDistinct(recipeIngredient.get("ingredient")),
                            containsIngredients.length));
            return criteriaBuilder.in(root).value(subquery);
        }
    }
}
