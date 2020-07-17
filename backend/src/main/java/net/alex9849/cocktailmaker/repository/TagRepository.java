package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
