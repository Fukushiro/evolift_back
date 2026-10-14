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

	@Column(name = "video_url", length = 2048)
	private String videoUrl;

	protected Exercicio() {
	}

	public Exercicio(String nome, String videoUrl) {
		this.nome = nome;
		this.videoUrl = videoUrl;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void update(String nome, String videoUrl) {
		this.nome = nome;
		this.videoUrl = videoUrl;
	}
}
