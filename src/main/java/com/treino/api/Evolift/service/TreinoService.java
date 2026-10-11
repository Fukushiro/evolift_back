package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.TreinoRequest;
import com.treino.api.Evolift.model.Treino;
import com.treino.api.Evolift.repository.TreinoRepository;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TreinoService {

	private final TreinoRepository treinoRepository;

	public TreinoService(TreinoRepository treinoRepository) {
		this.treinoRepository = treinoRepository;
	}

	public List<Treino> findAll() {
		return treinoRepository.findAll();
	}

	public Treino findById(Long id) {
		return treinoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino not found"));
	}

	public Treino create(TreinoRequest request) {
		validate(request);
		return treinoRepository.save(
				request.nome().trim(),
				request.quantidadeSeries(),
				request.quantidadeExecucoes());
	}

	public Treino update(Long id, TreinoRequest request) {
		validate(request);
		if (!treinoRepository.update(
				id,
				request.nome().trim(),
				request.quantidadeSeries(),
				request.quantidadeExecucoes())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino not found");
		}
		return findById(id);
	}

	public void delete(Long id) {
		if (!treinoRepository.deleteById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino not found");
		}
	}

	private void validate(TreinoRequest request) {
		if (request == null || request.nome() == null || request.nome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome is required");
		}
		if (request.quantidadeSeries() == null || request.quantidadeSeries() < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade de séries deve ser maior que zero");
		}
		if (request.quantidadeExecucoes() == null || request.quantidadeExecucoes() < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade de execuções deve ser maior que zero");
		}
	}
}
