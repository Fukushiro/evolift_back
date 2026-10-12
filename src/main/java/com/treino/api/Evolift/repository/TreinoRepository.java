package com.treino.api.Evolift.repository;

import com.treino.api.Evolift.model.AppUser;
import com.treino.api.Evolift.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

	@EntityGraph(attributePaths = { "exercicios", "exercicios.exercicio" })
	List<Treino> findAllByAppUser(AppUser appUser);

	@EntityGraph(attributePaths = { "exercicios", "exercicios.exercicio" })
	Optional<Treino> findByIdAndAppUserId(Long id, Long appUserId);

	@Override
	@EntityGraph(attributePaths = { "exercicios", "exercicios.exercicio" })
	List<Treino> findAll();

	@Override
	@EntityGraph(attributePaths = { "exercicios", "exercicios.exercicio" })
	Optional<Treino> findById(Long id);
}
