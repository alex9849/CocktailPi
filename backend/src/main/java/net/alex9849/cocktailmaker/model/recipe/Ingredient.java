package net.alex9849.cocktailmaker.model.recipe;

import com.fasterxml.jackson.annotation.JsonValue;
import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.Pump;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ingredients", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Ingredient implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    @NotNull
    @Min(0) @Max(100)
    private int alcoholContent;

    @OneToMany(mappedBy = "ingredient")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RecipeIngredient> recipeIngredients;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_owned_ingredients",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Category> owningUsers;

    @OneToMany(mappedBy = "currentIngredient",fetch = FetchType.LAZY)
    List<Pump> pumps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public abstract Unit getUnit();

    public enum Unit {
        MILLILITER("ml"), GRAM("g"), LEVELED_TEASPOON("teaspoon(s)"), LEVELED_TABLESPOON("tablespoon(s)");
        private final String displayUnit;

        Unit(String displayUnit) {
            this.displayUnit = displayUnit;
        }

        @JsonValue
        public String getDisplayUnit() {
            return displayUnit;
        }

        public static Unit findByDisplayUnit(String displayUnit) {
            for(Unit current : Unit.values()) {
                if(current.getDisplayUnit().equals(displayUnit)) {
                    return current;
                }
            }
            return null;
        }

    }

    public static class IngredientFilterNoFilter implements Specification<Ingredient> {

        @Override
        public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.and();
        }
    }

    public static class IngredientFilterNameIncludes implements Specification<Ingredient> {
        private String includes;
        private boolean ignoreCase;

        public IngredientFilterNameIncludes(String includes, boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            this.includes = includes;
        }

        @Override
        public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            if(ignoreCase) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + includes.toLowerCase() + "%");
            } else {
                return criteriaBuilder.like(root.get("name"), "%" + includes + "%");
            }
        }
    }
}
