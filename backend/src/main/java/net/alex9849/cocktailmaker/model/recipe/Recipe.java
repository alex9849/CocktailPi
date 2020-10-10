package net.alex9849.cocktailmaker.model.recipe;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

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

    @Lob() @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @NotNull
    @Size(min = 0, max = 3000)
    private String description;


    @OneToMany(mappedBy = "recipe", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RecipeIngredient> recipeIngredients;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "recipe_tags",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public static class RecipeFilterNoFilter implements Specification<Recipe> {

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.and();
        }
    }

    public static class RecipeFilterPublic implements Specification<Recipe> {
        private boolean isPublic;

        public RecipeFilterPublic(boolean isPublic) {
            super();
            this.isPublic = isPublic;
        }

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.equal(root.get("inPublic"), isPublic);
        }
    }

    public static class RecipeFilterOwnerId implements Specification<Recipe> {
        private long userId;

        public RecipeFilterOwnerId(long userId) {
            this.userId = userId;
        }

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.equal(root.get("owner").<String>get("id"), userId);
        }
    }

    public static class RecipeFilterNameContain implements Specification<Recipe> {
        String searchName;

        public RecipeFilterNameContain(String searchName) {
            this.searchName = searchName.toLowerCase();
        }

        @Override
        public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchName + "%");
        }
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
}
