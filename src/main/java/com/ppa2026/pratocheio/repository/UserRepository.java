package com.ppa2026.pratocheio.repository;

import com.ppa2026.pratocheio.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByNameAndPassword(String name, String password);
}
