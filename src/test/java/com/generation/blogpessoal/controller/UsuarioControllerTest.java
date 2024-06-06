package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@eoot.com", "rootroot", ""));
	}

	@Test
	@Order(1)
	@DisplayName("Deve cadastrar um novo usuario")
	public void DeveCriarNovoUsuario() {

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}

	@Test
	@Order(2)
	@DisplayName ("Nao deve permitir duplicacao do Usuirio")
	public void naoDeveDuplicarUsuario() {

		usuarioService. cadastrarUsuario(new Usuario(0L,
				"Bernardo Henrique", "bernardo@gmail.com", "13465278", "-"));
	
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Bernardo Henrique", "bernardo@gmail.com", "13465278", "-"));
	
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals (HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	
	}
	
	@Test
	@Order(3)
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario (new Usuario(0L, 
				"Victor Carvalho", "victor@gmail.com", "victor123", "-"));
			
		Usuario usuarioUpdate = new Usuario (usuarioCadastrado.get().getId(), 
				"Victor Henrique", "victorhenrique@gmail.com", "victor123","-");
	
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> (usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate 
				.withBasicAuth("root@eoot.com", "rootroot") 
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
	
		assertEquals (HttpStatus.OK, corpoResposta.getStatusCode());
	
	}
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodorsUsuarios() {

		usuarioService.cadastrarUsuario (new Usuario(0L, 
				"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
	
		usuarioService.cadastrarUsuario (new Usuario(0L, 
				"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));
	
		ResponseEntity<String> resposta = testRestTemplate 
				.withBasicAuth("root@eoot.com", "rootroot") 
				.exchange("/usuarios/all", HttpMethod. GET, null, String.class);
	
		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}
}