package com.blogpessoal.blogpessoal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blogpessoal.blogpessoal.model.Usuario;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)//vai ver se a porta 8080 estiver ocupada, ela abre outra pra teste
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll //antes de testar, deleta tds usuarios e insere os novos abaixo
    void start(){
    usuarioRepository.deleteAll();

    usuarioRepository.save(new Usuario(0L, "João da Silva", "joao@email.com.br", "https://i.imgur.com/FETvs2O.jpg", "13465278"));

    usuarioRepository.save(new Usuario(0L, "Manuela da Silva", "manuela@email.com.br", "https://i.imgur.com/NtyGneo.jpg", "13465278"));

    usuarioRepository.save(new Usuario(0L, "Adriana da Silva", "adriana@email.com.br", "https://i.imgur.com/mB3VM2N.jpg", "13465278"));

    usuarioRepository.save(new Usuario(0L, "Paulo Antunes", "paulo@email.com.br", "https://i.imgur.com/JR7kUFU.jpg", "13465278"));
    }

    @Test
    @DisplayName("Retorna 1 usuario")
    public void deveRetornarUmUsuario() {

        Optional<Usuario> usuario = usuarioRepository.findByUsuario("joao@email.com.br");

        assertTrue(usuario.get().getUsuario().equals("joao@email.com.br"));
    }

    @Test
    @DisplayName("Retorna 3 usuarios")
    public void deveRetornarTresUsuarios() {

        List<Usuario> listaDeUsuarios = usuarioRepository.findAllByNomeContainingIgnoreCase("Silva");

        assertEquals(3, listaDeUsuarios.size());

        assertTrue(listaDeUsuarios.get(0).getNome().equals("João da Silva"));//busca o usuario da posição 0, pega o nome dele, e verifica se é o João..., e assim em diante
        assertTrue(listaDeUsuarios.get(1).getNome().equals("Manuela da Silva"));
        assertTrue(listaDeUsuarios.get(2).getNome().equals("Adriana da Silva"));

    }


    @AfterAll
    public void end() {
        usuarioRepository.deleteAll();
    }

}
