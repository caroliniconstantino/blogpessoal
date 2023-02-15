package com.blogpessoal.blogpessoal.controller;

import com.blogpessoal.blogpessoal.model.Tema;
import com.blogpessoal.blogpessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TemaController {

    @Autowired
    private TemaRepository temaRepository;

    @GetMapping
    public ResponseEntity<List<Tema>> getAll(){
        return ResponseEntity.ok(temaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tema> getById(@PathVariable Long id){
        return temaRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/descricao/{descricao}")
    public ResponseEntity<List<Tema>> getByTitle(@PathVariable String descricao){
        return ResponseEntity.ok(temaRepository
                .findAllByDescricaoContainingIgnoreCase(descricao));
    }

    @PostMapping
    public ResponseEntity<Tema> post(@Valid @RequestBody Tema tema){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(temaRepository.save(tema));
    }
//NO CASO ABAIXO, O ID ESTÁ SENDO ENVIANDO POR PATH
//    @PutMapping("/{id}")
//    public ResponseEntity<Tema> put(@Valid @RequestBody @PathVariable Tema tema) {
//
//            if(temaRepository.existsById(tema.getId()))
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(temaRepository.save(tema));
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }

    @PutMapping
    public ResponseEntity<Tema> put(@Valid @RequestBody Tema tema) {
        //@Valid verifica se tds as exigências da tabela foram cumpridas (not null, size...)
        // @RequestBody que a requesição requer que seja enviado um body
        //temaRepository é o banco de dados

        if(temaRepository.existsById(tema.getId()))//NESSE CASO, O ID ESTÁ SENDO ENVIADO DENTRO DO BODY
            return ResponseEntity.status(HttpStatus.OK)
                    .body(temaRepository.save(tema));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        Optional<Tema> tema = temaRepository.findById(id);

        if(tema.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        temaRepository.deleteById(id);
    }
}
