package com.treino.api.Evolift.dto;

import com.treino.api.Evolift.model.Role;

public record AuthResponse(Long id, String username, String message, Role role) {
}
