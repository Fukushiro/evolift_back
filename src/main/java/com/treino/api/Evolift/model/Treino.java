package com.treino.api.Evolift.model;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "treinos")
public class Treino {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "app_user_id", nullable = false)
	private AppUser appUser;

	@OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TreinoExercicio> exercicios = new ArrayList<>();

	protected Treino() {
	}

	public Treino(String nome) {
		this.nome = nome;
	}

	public Treino(String nome, AppUser appUser) {
		this.nome = nome;
		this.appUser = appUser;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public List<TreinoExercicio> getExercicios() {
		return exercicios;
	}

	public void update(String nome) {
		this.nome = nome;
	}

	public void removeExerciciosExcept(Set<Long> exercicioIds) {
		exercicios.removeIf(item -> !exercicioIds.contains(item.getExercicio().getId()));
	}

	public void addExercicio(Exercicio exercicio, Integer quantidadeSeries, Integer quantidadeExecucoes) {
		exercicios.add(new TreinoExercicio(this, exercicio, quantidadeSeries, quantidadeExecucoes));
	}

	public TreinoExercicio findTreinoExercicio(Long exercicioId) {
		return exercicios.stream()
				.filter(item -> item.getExercicio().getId().equals(exercicioId))
				.findFirst()
				.orElse(null);
	}
}
