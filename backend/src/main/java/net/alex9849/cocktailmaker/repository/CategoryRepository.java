package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findAllByNameIgnoringCase(String name);

}
