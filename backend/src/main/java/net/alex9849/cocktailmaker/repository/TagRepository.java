package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.recipe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
