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

    // Alteração para UUID no PathVariable
    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(@PathVariable UUID id) {
        Optional<Estabelecimento> estabelecimento = estabelecimentoRepository.findById(id);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Estabelecimento criar(@RequestBody Estabelecimento estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizar(@PathVariable UUID id, @RequestBody Estabelecimento estabelecimentoAtualizado) {
        return estabelecimentoRepository.findById(id).map(estabelecimento -> {
            estabelecimento.setNome(estabelecimentoAtualizado.getNome());
            estabelecimento.setEndereco(estabelecimentoAtualizado.getEndereco());
            estabelecimento.setServicos(estabelecimentoAtualizado.getServicos()); // Atualiza servicos
            estabelecimento.setProfissionais(estabelecimentoAtualizado.getProfissionais()); // Atualiza profissionais
            estabelecimento.setHorariosFuncionamento(estabelecimentoAtualizado.getHorariosFuncionamento());
            estabelecimento.setFotos(estabelecimentoAtualizado.getFotos());
            return ResponseEntity.ok(estabelecimentoRepository.save(estabelecimento));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Alteração para UUID no PathVariable
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