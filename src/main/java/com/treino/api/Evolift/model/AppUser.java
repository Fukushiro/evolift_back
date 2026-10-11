package com.treino.api.Evolift.model;

import java.time.LocalDateTime;

public record AppUser(Long id, String username, String passwordHash, LocalDateTime createdAt) {
}
