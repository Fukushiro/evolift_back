package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.ExercicioRequest;
import com.treino.api.Evolift.model.Exercicio;
import com.treino.api.Evolift.repository.ExercicioRepository;
import com.treino.api.Evolift.repository.TreinoExercicioRepository;
import java.net.URI;
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
		return exercicioRepository.save(new Exercicio(validateNome(request), validateVideoUrl(request)));
	}

	public Exercicio update(Long id, ExercicioRequest request) {
		String nome = validateNome(request);
		String videoUrl = validateVideoUrl(request);
		Exercicio exercicio = findById(id);
		exercicio.update(nome, videoUrl);
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

	private String validateVideoUrl(ExercicioRequest request) {
		if (request == null || request.videoUrl() == null || request.videoUrl().isBlank()) {
			return null;
		}
		String videoUrl = request.videoUrl().trim();
		if (videoUrl.length() > 2048) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video URL must not exceed 2048 characters");
		}
		try {
			URI uri = URI.create(videoUrl);
			String scheme = uri.getScheme();
			if (!uri.isAbsolute() || uri.getHost() == null
					|| !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video URL must be an HTTP or HTTPS URL");
			}
		}
		catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video URL is invalid", exception);
		}
		return videoUrl;
	}

}
