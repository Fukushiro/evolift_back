package com.treino.api.Evolift.repository;

import com.treino.api.Evolift.model.AppUser;
import com.treino.api.Evolift.model.Role;
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
					select id, username, password_hash, created_at, role
					from app_users
					where username = :username
					""")
					.param("username", username)
					.query((resultSet, rowNum) -> new AppUser(
							resultSet.getLong("id"),
							resultSet.getString("username"),
							resultSet.getString("password_hash"),
							resultSet.getTimestamp("created_at").toLocalDateTime(),
							Role.valueOf(resultSet.getString("role"))))
					.single();

			return Optional.of(user);
		}
		catch (EmptyResultDataAccessException exception) {
			return Optional.empty();
		}
	}

	public AppUser save(String username, String passwordHash) {
		jdbcClient.sql("""
				insert into app_users (username, password_hash, role)
				values (:username, :passwordHash, :role)
				""")
				.param("username", username)
				.param("passwordHash", passwordHash)
				.param("role", Role.USUARIO.name())
				.update();

		return findByUsername(username).orElseThrow();
	}
}
