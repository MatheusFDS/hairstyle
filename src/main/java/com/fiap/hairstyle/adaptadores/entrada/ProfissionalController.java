package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.repositorios.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @GetMapping
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    // Alteração para UUID no PathVariable
    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable UUID id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        return profissional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Profissional criar(@RequestBody Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    // Alteração para UUID no PathVariable
    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(@PathVariable UUID id, @RequestBody Profissional profissionalAtualizado) {
        return profissionalRepository.findById(id).map(profissional -> {
            profissional.setNome(profissionalAtualizado.getNome());
            profissional.setEspecialidade(profissionalAtualizado.getEspecialidade());
            profissional.setTelefone(profissionalAtualizado.getTelefone());
            profissional.setTarifa(profissionalAtualizado.getTarifa());
            profissional.setEstabelecimento(profissionalAtualizado.getEstabelecimento());
            return ResponseEntity.ok(profissionalRepository.save(profissional));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Alteração para UUID no PathVariable
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (profissionalRepository.existsById(id)) {
            profissionalRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
