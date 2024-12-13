package org.example.doctorai.repository;

import org.example.doctorai.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
}
