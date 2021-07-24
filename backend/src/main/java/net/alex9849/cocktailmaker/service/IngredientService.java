package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.AutomatedIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ManualIngredientDto;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PumpService pumpService;

    @Autowired
    private EntityManager entityManager;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith) {
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(nameStartsWith != null) {
            idsToFindSetList.add(ingredientRepository.findIdsAutocompleteName(nameStartsWith));
        }

        if(idsToFindSetList.isEmpty()) {
            return ingredientRepository.findAll();
        }

        Set<Long> retained = null;
        for(Set<Long> current : idsToFindSetList) {
            if(retained == null) {
                retained = current;
                continue;
            }
            retained.retainAll(current);
        }
        if(retained.isEmpty()) {
            return Collections.emptyList();
        }
        return ingredientRepository.findByIds(retained.toArray(new Long[1]));
    }

    public List<Ingredient> getIngredientsOwnedByUSer(long userId) {
        return ingredientRepository.findByIds(ingredientRepository
                .findIdsOwnedByUser(userId).toArray(new Long[1]));
    }

    public Ingredient fromDto(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        if(ingredientDto instanceof ManualIngredientDto) {
            ManualIngredient ingredient = new ManualIngredient();
            BeanUtils.copyProperties(ingredientDto, ingredient);
            return ingredient;
        }
        if(ingredientDto instanceof AutomatedIngredientDto) {
            AutomatedIngredient ingredient = new AutomatedIngredient();
            BeanUtils.copyProperties(ingredientDto, ingredient);
            return ingredient;
        }
        throw new IllegalStateException("IngredientType not supported yet!");
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(!ingredientRepository.findById(ingredient.getId()).isPresent()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            if(!Objects.equals(optionalIngredient.get().getId(), ingredient.getId())) {
                throw new IllegalArgumentException("An ingredient with this name already exists!");
            }
            if(optionalIngredient.get().getClass() != ingredient.getClass()) {
                Session session = (Session) entityManager.getDelegate();
                session.evict(optionalIngredient.get());
                Query query = entityManager.createNativeQuery("UPDATE ingredients SET dtype = :dtype WHERE id = :id");
                query.setParameter("dtype", ingredient.getClass().getAnnotation(DiscriminatorValue.class).value());
                query.setParameter("id", ingredient.getId());
                query.executeUpdate();
                entityManager.merge(ingredient);
                entityManager.persist(ingredient);
                entityManager.clear();
            }
        }
        ingredientRepository.update(ingredient);
        return ingredient;
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        ingredient.setId(null);
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.create(ingredient);
    }

    public boolean deleteIngredient(long id) {
        return ingredientRepository.delete(id);
    }
}
