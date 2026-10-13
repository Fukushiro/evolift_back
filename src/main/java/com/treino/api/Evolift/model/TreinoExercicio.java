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

	@Column(nullable = false)
	private Integer posicao = 1;

	protected TreinoExercicio() {
	}

	public TreinoExercicio(Treino treino, Exercicio exercicio, Integer quantidadeSeries,
			Integer quantidadeExecucoes, Integer posicao) {
		this.treino = treino;
		this.exercicio = exercicio;
		this.quantidadeSeries = quantidadeSeries;
		this.quantidadeExecucoes = quantidadeExecucoes;
		this.posicao = posicao;
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

	public Integer getPosicao() {
		return posicao;
	}

	public void update(Integer quantidadeSeries, Integer quantidadeExecucoes, Integer posicao) {
		this.quantidadeSeries = quantidadeSeries;
		this.quantidadeExecucoes = quantidadeExecucoes;
		this.posicao = posicao;
	}
}
