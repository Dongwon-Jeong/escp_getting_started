package com.midasit.midascafe.repository;

import com.midasit.midascafe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);

    @Override
    List<User> findAll();
}
