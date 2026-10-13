package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.ExercicioRequest;
import com.treino.api.Evolift.model.Exercicio;
import com.treino.api.Evolift.repository.ExercicioRepository;
import com.treino.api.Evolift.repository.TreinoExercicioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExercicioService {

	private final ExercicioRepository exercicioRepository;
	private final TreinoExercicioRepository treinoExercicioRepository;

	public ExercicioService(ExercicioRepository exercicioRepository,
			TreinoExercicioRepository treinoExercicioRepository) {
		this.exercicioRepository = exercicioRepository;
		this.treinoExercicioRepository = treinoExercicioRepository;
	}

	public List<Exercicio> findAll() {
		return exercicioRepository.findAll();
	}

	public Exercicio findById(Long id) {
		return exercicioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
	}

	public Exercicio create(ExercicioRequest request) {
		return exercicioRepository.save(new Exercicio(validateNome(request)));
	}

	public Exercicio update(Long id, ExercicioRequest request) {
		String nome = validateNome(request);
		Exercicio exercicio = findById(id);
		exercicio.update(nome);
		return exercicioRepository.save(exercicio);
	}

	public void delete(Long id) {
		Exercicio exercicio = findById(id);
		if (treinoExercicioRepository.existsByExercicio_Id(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete an exercise used in a workout");
		}
		exercicioRepository.delete(exercicio);
	}

	private String validateNome(ExercicioRequest request) {
		if (request == null || request.nome() == null || request.nome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome is required");
		}
		return request.nome().trim();
	}

}
