package com.treino.api.Evolift.dto;

import java.util.List;

public record TreinoRequest(String nome, Long appUserId, List<TreinoExercicioRequest> exercicios) {
}
