package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    // Check if username is already taken before creating a new operator
    boolean existsByUsername(String username);
}