package net.alex9849.cocktailmaker.model.recipe;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "ingredients")
public class Ingredient implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

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

    public static class IngredientFilterNoFilter implements Specification<Ingredient> {

        @Override
        public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.and();
        }
    }

    public static class IngredientFilterStartsWith implements Specification<Ingredient> {
        private String startsWith;
        private boolean ignoreCase;

        public IngredientFilterStartsWith(String startwith, boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            this.startsWith = startwith;
        }

        @Override
        public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            if(ignoreCase) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), startsWith.toLowerCase() + "%");
            } else {
                return criteriaBuilder.like(root.get("name"), startsWith + "%");
            }
        }
    }
}
