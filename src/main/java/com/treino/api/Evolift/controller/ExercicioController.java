package com.treino.api.Evolift.controller;

import com.treino.api.Evolift.dto.ExercicioRequest;
import com.treino.api.Evolift.model.Exercicio;
import com.treino.api.Evolift.service.ExercicioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercicios")
public class ExercicioController {

	private final ExercicioService exercicioService;

	public ExercicioController(ExercicioService exercicioService) {
		this.exercicioService = exercicioService;
	}

	@GetMapping
	public List<Exercicio> findAll() {
		return exercicioService.findAll();
	}

	@GetMapping("/{id}")
	public Exercicio findById(@PathVariable Long id) {
		return exercicioService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Exercicio create(@RequestBody ExercicioRequest request) {
		return exercicioService.create(request);
	}
}
