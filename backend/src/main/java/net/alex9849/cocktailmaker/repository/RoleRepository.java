package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.ERole;
import net.alex9849.cocktailmaker.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

}
