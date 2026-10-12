package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.ExercicioRequest;
import com.treino.api.Evolift.model.Exercicio;
import com.treino.api.Evolift.repository.ExercicioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExercicioService {

	private final ExercicioRepository exercicioRepository;

	public ExercicioService(ExercicioRepository exercicioRepository) {
		this.exercicioRepository = exercicioRepository;
	}

	public List<Exercicio> findAll() {
		return exercicioRepository.findAll();
	}

	public Exercicio findById(Long id) {
		return exercicioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
	}

	public Exercicio create(ExercicioRequest request) {
		if (request == null || request.nome() == null || request.nome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome is required");
		}
		return exercicioRepository.save(new Exercicio(request.nome().trim()));
	}
}
