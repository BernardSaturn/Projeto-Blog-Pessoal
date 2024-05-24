package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // essa classe vai se tornar uma entidade do banco de dados
@Table(name="tb_postagens")// Estou nomeando a tabela no banco de dados de tb_postagem
public class Postagem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "o atributo TITULO é obrigatório!!") //validation - validar nosso atributo NN para que ele não deixe de ser passado\informado
	@Size(min = 5, max = 100, message = " O atributo TITULO deve ter no minimo 5 caracteres e no maximo 100.")
	private String titulo;
	
	@NotBlank(message = "O atributo TEXTO é obrigatório!!")
	@Size(min = 10, max = 1000, message = " O atributo TEXTO deve ter no minimo 10 caracteres e no maximo 1000.")	
	private String texto;
	
	@UpdateTimestamp // vai pegar a hora do banco de dados e colocar automaticamente no campo
	private LocalDateTime data; // é um tipo de dado que já vem com padrão de data configurado
	
	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Tema tema;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Tema getTema() {
		return tema;
	}
	
	public void setTema(Tema tema) {
		this.tema = tema;
	}
	
}
