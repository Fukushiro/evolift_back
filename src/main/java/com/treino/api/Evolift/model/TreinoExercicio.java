package com.treino.api.Evolift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "treino_exercicios")
public class TreinoExercicio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "treino_id", nullable = false)
	private Treino treino;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "exercicio_id", nullable = false)
	private Exercicio exercicio;

	@Column(name = "quantidade_series", nullable = false)
	private Integer quantidadeSeries;

	@Column(name = "quantidade_execucoes", nullable = false)
	private Integer quantidadeExecucoes;

	protected TreinoExercicio() {
	}

	public TreinoExercicio(Treino treino, Exercicio exercicio, Integer quantidadeSeries,
			Integer quantidadeExecucoes) {
		this.treino = treino;
		this.exercicio = exercicio;
		this.quantidadeSeries = quantidadeSeries;
		this.quantidadeExecucoes = quantidadeExecucoes;
	}

	public Long getId() {
		return id;
	}

	public Exercicio getExercicio() {
		return exercicio;
	}

	public Integer getQuantidadeSeries() {
		return quantidadeSeries;
	}

	public Integer getQuantidadeExecucoes() {
		return quantidadeExecucoes;
	}

	public void updateQuantidades(Integer quantidadeSeries, Integer quantidadeExecucoes) {
		this.quantidadeSeries = quantidadeSeries;
		this.quantidadeExecucoes = quantidadeExecucoes;
	}
}
