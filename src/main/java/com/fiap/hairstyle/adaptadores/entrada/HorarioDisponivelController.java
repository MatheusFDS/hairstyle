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
@RequestMapping("/api/horarios-disponiveis")
public class HorarioDisponivelController {

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    // Listar horários disponíveis por ID do profissional
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<HorarioDisponivel>> listarPorProfissional(@PathVariable UUID profissionalId) {
        List<HorarioDisponivel> horarios = horarioDisponivelRepository.findByProfissionalId(profissionalId);
        return ResponseEntity.ok(horarios);
    }

    // Criar um novo horário disponível para um profissional
    @PostMapping("/profissional/{profissionalId}")
    public ResponseEntity<HorarioDisponivel> criarHorario(@PathVariable UUID profissionalId, @RequestBody HorarioDisponivel horario) {
        Optional<Profissional> profissionalOpt = profissionalRepository.findById(profissionalId);
        if (profissionalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        horario.setProfissional(profissionalOpt.get());
        HorarioDisponivel novoHorario = horarioDisponivelRepository.save(horario);
        return ResponseEntity.ok(novoHorario);
    }

    // Atualizar um horário específico
    @PutMapping("/{horarioId}")
    public ResponseEntity<HorarioDisponivel> atualizarHorario(@PathVariable UUID horarioId, @RequestBody HorarioDisponivel horarioAtualizado) {
        Optional<HorarioDisponivel> horarioOpt = horarioDisponivelRepository.findById(horarioId);
        if (horarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HorarioDisponivel horarioExistente = horarioOpt.get();
        horarioExistente.setDiaSemana(horarioAtualizado.getDiaSemana());
        horarioExistente.setHoraInicio(horarioAtualizado.getHoraInicio());
        horarioExistente.setHoraFim(horarioAtualizado.getHoraFim());

        HorarioDisponivel horarioAtualizadoBD = horarioDisponivelRepository.save(horarioExistente);
        return ResponseEntity.ok(horarioAtualizadoBD);
    }

    // Excluir um horário específico
    @DeleteMapping("/{horarioId}")
    public ResponseEntity<Void> deletarHorario(@PathVariable UUID horarioId) {
        if (horarioDisponivelRepository.existsById(horarioId)) {
            horarioDisponivelRepository.deleteById(horarioId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
