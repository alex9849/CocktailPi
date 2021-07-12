package net.alex9849.cocktailmaker.repository;


import net.alex9849.cocktailmaker.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameIgnoringCase(String username);

    List<User> findAll();
}
