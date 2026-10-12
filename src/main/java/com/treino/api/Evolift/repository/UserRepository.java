package com.treino.api.Evolift.repository;

import com.treino.api.Evolift.model.AppUser;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

	boolean existsByUsername(String username);

	Optional<AppUser> findByUsername(String username);
}
