package org.project.moodbotbackend.repository;

import org.project.moodbotbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String identifier);
    User findUserByUsername(String identifier);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
