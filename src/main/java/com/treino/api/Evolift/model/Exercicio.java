package com.treino.api.Evolift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercicios")
public class Exercicio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false)
	private Integer posicao = 1;

	protected Exercicio() {
	}

	public Exercicio(String nome) {
		this(nome, 1);
	}

	public Exercicio(String nome, Integer posicao) {
		this.nome = nome;
		this.posicao = posicao;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Integer getPosicao() {
		return posicao;
	}

	public void update(String nome, Integer posicao) {
		this.nome = nome;
		this.posicao = posicao;
	}
}
