package com.generation.blogpessoal.controller;

import java.util.List; 
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository; 

import jakarta.validation.Valid;

@RestController // Marca a classe como um controlador RESTful
@RequestMapping("/temas") // Define o caminho base para as rotas deste controlador
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite requisições de qualquer origem
public class TemaController {
    
    @Autowired // Injeta automaticamente uma instância de TemaRepository
    private TemaRepository temaRepository;
    
    // Método para obter todos os temas
    @GetMapping // Mapeia requisições GET para este método
    public ResponseEntity<List<Tema>> getAll(){ // Retorna uma lista de temas
        return ResponseEntity.ok(temaRepository.findAll()); // Envia a lista de temas com sucesso
    }
    
    // Método para obter um tema pelo ID
    @GetMapping("/{id}") // Mapeia requisições GET para este método, capturando o ID da URL
    public ResponseEntity<Tema> getById(@PathVariable Long id){ // Recebe um ID como parâmetro
        return temaRepository.findById(id) // Procura o tema pelo ID
           .map(resposta -> ResponseEntity.ok(resposta)) // Se encontrar, retorna o tema com sucesso
           .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se não encontrar, retorna erro 404
    }
    
    // Método para obter temas por descrição
    @GetMapping("/descricao/{descricao}") // Mapeia requisições GET para este método, capturando a descrição da URL
    public ResponseEntity<List<Tema>> getByDescricao(@PathVariable String descricao){ // Recebe uma descrição como parâmetro
        return ResponseEntity.ok(temaRepository.findAllByDescricaoContainingIgnoreCase(descricao)); // Retorna temas que contêm a descrição fornecida, ignorando maiúsculas e minúsculas
    }
    
    // Método para criar um novo tema
    @PostMapping // Mapeia requisições POST para este método
    public ResponseEntity<Tema> post(@Valid @RequestBody Tema tema){ // Recebe um objeto Tema no corpo da requisição
        return ResponseEntity.status(HttpStatus.CREATED) // Retorna um status 201 Created
               .body(temaRepository.save(tema)); // Salva o tema no banco de dados e retorna o tema criado
    }
    
    // Método para atualizar um tema
    @PutMapping // Mapeia requisições PUT para este método
    public ResponseEntity<Tema> put(@Valid @RequestBody Tema tema){ // Recebe um objeto Tema no corpo da requisição
        return temaRepository.findById(tema.getId()) // Procura o tema pelo ID
           .map(resposta -> ResponseEntity.status(HttpStatus.CREATED) // Se encontrar, retorna o tema atualizado com status 201 Created
           .body(temaRepository.save(tema))) // Salva o tema atualizado no banco de dados
           .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se não encontrar, retorna erro 404
    }
    
    // Método para deletar um tema
    @ResponseStatus(HttpStatus.NO_CONTENT) // Define o status HTTP da resposta como 204 No Content
    @DeleteMapping("/{id}") // Mapeia requisições DELETE para este método, capturando o ID da URL
    public void delete(@PathVariable Long id) { // Recebe um ID como parâmetro
        Optional<Tema> tema = temaRepository.findById(id); // Procura o tema pelo ID
        
        if(tema.isEmpty()) // Se o tema não foi encontrado
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Lança uma exceção com status 404 Not Found
        
        temaRepository.deleteById(id); // Deleta o tema pelo ID
    }
}
