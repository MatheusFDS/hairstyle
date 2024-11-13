package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.repositorios.HorarioDisponivelRepository;
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

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @GetMapping
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable UUID id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        return profissional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Profissional criar(@RequestBody Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (profissionalRepository.existsById(id)) {
            profissionalRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para definir a disponibilidade de horários do profissional
    @PostMapping("/{id}/disponibilidade")
    public ResponseEntity<?> definirDisponibilidade(@PathVariable UUID id, @RequestBody List<HorarioDisponivel> horarios) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        if (profissional.isPresent()) {
            // Associa os horários ao profissional
            horarios.forEach(horario -> horario.setProfissional(profissional.get()));
            horarioDisponivelRepository.saveAll(horarios);
            return ResponseEntity.ok("Disponibilidade definida com sucesso.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para listar a disponibilidade de horários do profissional
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<List<HorarioDisponivel>> listarDisponibilidade(@PathVariable UUID id) {
        List<HorarioDisponivel> disponibilidade = horarioDisponivelRepository.findByProfissionalId(id);
        return ResponseEntity.ok(disponibilidade);
    }
}
