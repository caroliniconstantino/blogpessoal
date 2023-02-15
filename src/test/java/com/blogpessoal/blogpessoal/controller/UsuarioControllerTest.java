package com.blogpessoal.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blogpessoal.blogpessoal.model.Usuario;
import com.blogpessoal.blogpessoal.repository.UsuarioRepository;
import com.blogpessoal.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//vai ver se a porta 8080 estiver ocupada, ela abre outra pra teste
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate; //foi injetado um obj da classe Test... que as requisições para a nossa aplicação.

    @Autowired
    private UsuarioService usuarioService; //para persistir os objetos no Banco de dados de testes com a senha criptografada.

    @Autowired
    private UsuarioRepository usuarioRepository; //para limpar o Banco de dados de testes.

    @BeforeAll
    void start(){
        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(new Usuario(0l, "Rute", "rute@email.com", "sem foto", "abc"));
    }

    @Test
    @DisplayName("Cadastrar Um Usuário")
    public void deveCriarUmUsuario() {

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                "Paulo Antunes", "paulo_antunes@email.com.br", "https://i.imgur.com/JR7kUFU.jpg", "13465278"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Não deve permitir duplicação do Usuário")
    public void naoDeveDuplicarUsuario() {

        //no método abaixo, foi criado um novo usuario no banco de dados
        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "https://i.imgur.com/T12NIp9.jpg", "13465278"));

        //no método abaixo, foi criado um obj com novo usuario para ser usado no método da requisição abaixo
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                "Maria da Silva", "maria_silva@email.com.br", "https://i.imgur.com/T12NIp9.jpg", "13465278"));

        //método abaixo é a requisição teste. Precisa passar a url, tipo da requisição, o corpo da requisição, o conteúdo esperado
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        //no método abaixo, verifica se a resposta da requisição (Response), é a resposta esperada
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
    }

    @Test
    @DisplayName("Atualizar um Usuário")
    public void deveAtualizarUmUsuario() {

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Juliana Andrews", "juliana_andrews@email.com.br", "https://i.imgur.com/yDRVeK7.jpg", "juliana123"));

        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "https://i.imgur.com/yDRVeK7.jpg" , "juliana123");

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("rute@email.com", "abc")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
    }

    @Test
    @DisplayName("Listar todos os Usuários")
    public void deveMostrarTodosUsuarios() {

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Sabrina Sanches", "sabrina_sanches@email.com.br", "https://i.imgur.com/5M2p5Wb.jpg", "sabrina123"));

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Ricardo Marques", "ricardo_marques@email.com.br", "https://i.imgur.com/Sk5SjWE.jpg", "ricardo123"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("rute@email.com", "abc")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }

/*    @DisplayName("Buscar usuário pelo id")
    public void buscarUsuarioId() {

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Sabrina Sanches", "sabrina_sanches@email.com.br", "https://i.imgur.com/5M2p5Wb.jpg", "sabrina123"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("rute@email.com", "abc")
                .exchange("/usuarios/{id}", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    } */
}
