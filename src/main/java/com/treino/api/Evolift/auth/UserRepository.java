package com.treino.api.Evolift.auth;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	private final JdbcClient jdbcClient;

	public UserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public boolean existsByUsername(String username) {
		Integer count = jdbcClient.sql("select count(*) from app_users where username = :username")
				.param("username", username)
				.query(Integer.class)
				.single();

		return count != null && count > 0;
	}

	public Optional<AppUser> findByUsername(String username) {
		try {
			AppUser user = jdbcClient.sql("""
					select id, username, password_hash, created_at
					from app_users
					where username = :username
					""")
					.param("username", username)
					.query(AppUser.class)
					.single();

			return Optional.of(user);
		}
		catch (EmptyResultDataAccessException exception) {
			return Optional.empty();
		}
	}

	public AppUser save(String username, String passwordHash) {
		jdbcClient.sql("""
				insert into app_users (username, password_hash)
				values (:username, :passwordHash)
				""")
				.param("username", username)
				.param("passwordHash", passwordHash)
				.update();

		return findByUsername(username).orElseThrow();
	}
}
