package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.TreinoRequest;
import com.treino.api.Evolift.dto.TreinoExercicioRequest;
import com.treino.api.Evolift.model.AppUser;
import com.treino.api.Evolift.model.Exercicio;
import com.treino.api.Evolift.model.Treino;
import com.treino.api.Evolift.model.TreinoExercicio;
import com.treino.api.Evolift.repository.ExercicioRepository;
import com.treino.api.Evolift.repository.TreinoRepository;
import com.treino.api.Evolift.repository.UserRepository;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TreinoService {

	private final TreinoRepository treinoRepository;
	private final ExercicioRepository exercicioRepository;
	private final UserRepository userRepository;

	public TreinoService(TreinoRepository treinoRepository, ExercicioRepository exercicioRepository,
			UserRepository userRepository) {
		this.treinoRepository = treinoRepository;
		this.exercicioRepository = exercicioRepository;
		this.userRepository = userRepository;
	}

	public List<Treino> findAll() {
		AppUser appUser = findCurrentUserOrNull();
		if (appUser == null) {
			return treinoRepository.findAll();
		}
		return treinoRepository.findAllByAppUser(appUser);
	}

	public Treino findById(Long id) {
		AppUser appUser = findCurrentUserOrNull();
		if (appUser == null) {
			return treinoRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino not found"));
		}
		return treinoRepository.findByIdAndAppUserId(id, appUser.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino not found"));
	}

	@Transactional
	public Treino create(TreinoRequest request) {
		validate(request, true);
		AppUser appUser = findAppUserByIdOrThrow(request.appUserId());
		Treino treino = new Treino(request.nome().trim(), appUser);
		applyExercicios(treino, request.exercicios());
		return treinoRepository.save(treino);
	}

	@Transactional
	public Treino update(Long id, TreinoRequest request) {
		validate(request, false);
		Treino treino = findById(id);
		treino.update(request.nome().trim());
		if (request.appUserId() != null) {
			AppUser appUser = findAppUserByIdOrThrow(request.appUserId());
			treino.setAppUser(appUser);
		}
		applyExercicios(treino, request.exercicios());
		return treinoRepository.save(treino);
	}

	@Transactional
	public void delete(Long id) {
		Treino treino = findById(id);
		treinoRepository.delete(treino);
	}

	private AppUser findAppUserByIdOrThrow(Long appUserId) {
		if (appUserId == null || appUserId < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
		}
		return userRepository.findById(appUserId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + appUserId));
	}

	private AppUser findCurrentUserOrThrow() {
		AppUser appUser = findCurrentUserOrNull();
		if (appUser == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		return appUser;
	}

	private AppUser findCurrentUserOrNull() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
			return null;
		}
		return userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
	}

	private void validate(TreinoRequest request, boolean requireUserId) {
		if (request == null || request.nome() == null || request.nome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome is required");
		}
		if (requireUserId && request.appUserId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
		}
		if (request.appUserId() != null && request.appUserId() < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is invalid");
		}
		if (request.exercicios() == null || request.exercicios().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one exercise is required");
		}

		Set<Long> exercicioIds = new HashSet<>();
		for (TreinoExercicioRequest exercicio : request.exercicios()) {
			if (exercicio == null || exercicio.exercicioId() == null || exercicio.exercicioId() < 1) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise ID is required");
			}
			if (!exercicioIds.add(exercicio.exercicioId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate exercise in workout");
			}
			if (exercicio.quantidadeSeries() == null || exercicio.quantidadeSeries() < 1) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Quantidade de séries deve ser maior que zero");
			}
			if (exercicio.quantidadeExecucoes() == null || exercicio.quantidadeExecucoes() < 1) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Quantidade de execuções deve ser maior que zero");
			}
		}
	}

	private void applyExercicios(Treino treino, List<TreinoExercicioRequest> exercicios) {
		Set<Long> exercicioIds = new HashSet<>();
		for (TreinoExercicioRequest item : exercicios) {
			exercicioIds.add(item.exercicioId());
			TreinoExercicio existente = treino.findTreinoExercicio(item.exercicioId());
			if (existente != null) {
				existente.updateQuantidades(item.quantidadeSeries(), item.quantidadeExecucoes());
				continue;
			}
			Exercicio exercicio = exercicioRepository.findById(item.exercicioId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"Exercise not found: " + item.exercicioId()));
			treino.addExercicio(exercicio, item.quantidadeSeries(), item.quantidadeExecucoes());
		}
		treino.removeExerciciosExcept(exercicioIds);
	}
}
