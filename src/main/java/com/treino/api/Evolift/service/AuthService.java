package com.treino.api.Evolift.service;

import com.treino.api.Evolift.dto.AuthResponse;
import com.treino.api.Evolift.dto.LoginRequest;
import com.treino.api.Evolift.dto.RegisterRequest;
import com.treino.api.Evolift.model.AppUser;
import com.treino.api.Evolift.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	public AuthResponse register(RegisterRequest request) {
		validateCredentials(request.username(), request.password());

		if (userRepository.existsByUsername(request.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
		}

		String passwordHash = passwordEncoder.encode(request.password());
		AppUser user = userRepository.save(request.username(), passwordHash);

		return new AuthResponse(user.id(), user.username(), "User registered successfully", user.role());
	}

	public AuthResponse login(LoginRequest request) {
		validateCredentials(request.username(), request.password());

		AppUser user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

		if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}

		return new AuthResponse(user.id(), user.username(), "Login successful", user.role());
	}

	private void validateCredentials(String username, String password) {
		if (username == null || username.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
		}

		if (password == null || password.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
		}
	}
}
