package com.treino.api.Evolift.repository;

import com.treino.api.Evolift.model.TreinoExercicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoExercicioRepository extends JpaRepository<TreinoExercicio, Long> {

	boolean existsByExercicio_Id(Long exercicioId);
}
