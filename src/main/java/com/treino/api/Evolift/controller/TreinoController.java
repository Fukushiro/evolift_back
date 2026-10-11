package com.treino.api.Evolift.controller;

import com.treino.api.Evolift.dto.TreinoRequest;
import com.treino.api.Evolift.model.Treino;
import com.treino.api.Evolift.service.TreinoService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

	private final TreinoService treinoService;

	public TreinoController(TreinoService treinoService) {
		this.treinoService = treinoService;
	}

	@GetMapping
	public List<Treino> findAll() {
		return treinoService.findAll();
	}

	@GetMapping("/{id}")
	public Treino findById(@PathVariable Long id) {
		return treinoService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Treino create(@RequestBody TreinoRequest request) {
		return treinoService.create(request);
	}

	@PutMapping("/{id}")
	public Treino update(@PathVariable Long id, @RequestBody TreinoRequest request) {
		return treinoService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		treinoService.delete(id);
	}
}
