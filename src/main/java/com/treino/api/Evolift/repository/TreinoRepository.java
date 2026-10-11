package com.treino.api.Evolift.repository;

import com.treino.api.Evolift.model.Treino;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TreinoRepository {

	private final JdbcClient jdbcClient;
	private final JdbcTemplate jdbcTemplate;

	public TreinoRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Treino> findAll() {
		return jdbcClient.sql("""
				select id, nome, quantidade_series, quantidade_execucoes
				from treinos
				order by id
				""")
				.query(Treino.class)
				.list();
	}

	public Optional<Treino> findById(Long id) {
		return jdbcClient.sql("""
				select id, nome, quantidade_series, quantidade_execucoes
				from treinos
				where id = :id
				""")
				.param("id", id)
				.query(Treino.class)
				.optional();
	}

	public Treino save(String nome, int quantidadeSeries, int quantidadeExecucoes) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(
					"insert into treinos (nome, quantidade_series, quantidade_execucoes) values (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, nome);
			statement.setInt(2, quantidadeSeries);
			statement.setInt(3, quantidadeExecucoes);
			return statement;
		}, keyHolder);

		Number generatedId = keyHolder.getKey();
		if (generatedId == null) {
			throw new IllegalStateException("Database did not return the new workout ID");
		}
		return findById(generatedId.longValue()).orElseThrow();
	}

	public boolean update(Long id, String nome, int quantidadeSeries, int quantidadeExecucoes) {
		int updatedRows = jdbcClient.sql("""
				update treinos
				set nome = :nome,
					quantidade_series = :quantidadeSeries,
					quantidade_execucoes = :quantidadeExecucoes
				where id = :id
				""")
				.param("id", id)
				.param("nome", nome)
				.param("quantidadeSeries", quantidadeSeries)
				.param("quantidadeExecucoes", quantidadeExecucoes)
				.update();

		return updatedRows > 0;
	}

	public boolean deleteById(Long id) {
		int deletedRows = jdbcClient.sql("delete from treinos where id = :id")
				.param("id", id)
				.update();

		return deletedRows > 0;
	}
}
