package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.adaptadores.saida.repositorios.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos")
public class EstabelecimentoController {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @GetMapping
    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(@PathVariable UUID id) {
        Optional<Estabelecimento> estabelecimento = estabelecimentoRepository.findById(id);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Estabelecimento> criar(@RequestBody Estabelecimento estabelecimento) {
        // Ignorar relacionamentos durante o cadastro
        estabelecimento.setProfissionais(List.of());
        estabelecimento.setServicos(List.of());
        estabelecimento.setAvaliacoes(List.of());

        Estabelecimento novoEstabelecimento = estabelecimentoRepository.save(estabelecimento);
        return ResponseEntity.ok(novoEstabelecimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizar(@PathVariable UUID id, @RequestBody Estabelecimento estabelecimentoAtualizado) {
        return estabelecimentoRepository.findById(id).map(estabelecimento -> {
            estabelecimento.setNome(estabelecimentoAtualizado.getNome());
            estabelecimento.setEndereco(estabelecimentoAtualizado.getEndereco());
            estabelecimento.setServicos(estabelecimentoAtualizado.getServicos());
            estabelecimento.setProfissionais(estabelecimentoAtualizado.getProfissionais());
            estabelecimento.setHorariosFuncionamento(estabelecimentoAtualizado.getHorariosFuncionamento());
            estabelecimento.setFotos(estabelecimentoAtualizado.getFotos());
            return ResponseEntity.ok(estabelecimentoRepository.save(estabelecimento));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (estabelecimentoRepository.existsById(id)) {
            estabelecimentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
