package com.treino.api.Evolift.dto;

public record TreinoExercicioRequest(
		Long exercicioId,
		Integer quantidadeSeries,
		Integer quantidadeExecucoes,
		Integer posicao) {
}
