package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.adaptadores.saida.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.ProfissionalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/horarios-disponiveis")
@Tag(name = "Horários Disponíveis", description = "Operações relacionadas aos horários disponíveis dos profissionais")
public class HorarioDisponivelController {

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Operation(summary = "Listar horários disponíveis", description = "Retorna todos os horários disponíveis de um profissional específico.")
    @ApiResponse(responseCode = "200", description = "Horários disponíveis listados com sucesso")
    @GetMapping(value = "/profissional/{profissionalId}", produces = "application/json")
    public ResponseEntity<?> listarPorProfissional(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId) {
        try {
            List<HorarioDisponivel> horarios = horarioDisponivelRepository.findByProfissionalId(profissionalId);
            return ResponseEntity.ok(horarios);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao listar horários: " + ex.getMessage());
        }
    }

    @Operation(summary = "Criar horário disponível", description = "Cria um novo horário disponível para o profissional especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Horário disponível criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
            @ApiResponse(responseCode = "400", description = "Conflito de horário")
    })
    @PostMapping(value = "/profissional/{profissionalId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> criarHorario(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId,
            @RequestBody HorarioDisponivel horario) {
        try {
            Optional<Profissional> profissionalOpt = profissionalRepository.findById(profissionalId);
            if (profissionalOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Profissional não encontrado.");
            }

            // Validar conflitos de horário
            List<HorarioDisponivel> horariosExistentes = horarioDisponivelRepository.findByProfissionalId(profissionalId);
            boolean conflito = horariosExistentes.stream().anyMatch(h ->
                    h.getDiaSemana().equals(horario.getDiaSemana()) &&
                            (horario.getHoraInicio().isBefore(h.getHoraFim()) && horario.getHoraFim().isAfter(h.getHoraInicio()))
            );

            if (conflito) {
                return ResponseEntity.badRequest().body("O horário entra em conflito com outro já existente.");
            }

            // Associar o profissional ao horário
            horario.setProfissional(profissionalOpt.get());
            HorarioDisponivel novoHorario = horarioDisponivelRepository.save(horario);

            return ResponseEntity.status(201).body(novoHorario);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao criar o horário: " + ex.getMessage());
        }
    }

    @Operation(summary = "Atualizar horário disponível", description = "Atualiza um horário disponível específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Conflito de horário")
    })
    @PutMapping(value = "/{horarioId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> atualizarHorario(
            @Parameter(description = "ID do horário", required = true) @PathVariable UUID horarioId,
            @RequestBody HorarioDisponivel horarioAtualizado) {
        try {
            Optional<HorarioDisponivel> horarioOpt = horarioDisponivelRepository.findById(horarioId);
            if (horarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Horário não encontrado.");
            }

            HorarioDisponivel horarioExistente = horarioOpt.get();

            // Validar conflitos de horário
            List<HorarioDisponivel> horariosExistentes = horarioDisponivelRepository.findByProfissionalId(horarioExistente.getProfissional().getId());
            boolean conflito = horariosExistentes.stream()
                    .filter(h -> !h.getId().equals(horarioId)) // Ignorar o horário que está sendo atualizado
                    .anyMatch(h ->
                            h.getDiaSemana().equals(horarioAtualizado.getDiaSemana()) &&
                                    (horarioAtualizado.getHoraInicio().isBefore(h.getHoraFim()) && horarioAtualizado.getHoraFim().isAfter(h.getHoraInicio()))
                    );

            if (conflito) {
                return ResponseEntity.badRequest().body("O horário entra em conflito com outro já existente.");
            }

            horarioExistente.setDiaSemana(horarioAtualizado.getDiaSemana());
            horarioExistente.setHoraInicio(horarioAtualizado.getHoraInicio());
            horarioExistente.setHoraFim(horarioAtualizado.getHoraFim());

            HorarioDisponivel horarioAtualizadoBD = horarioDisponivelRepository.save(horarioExistente);
            return ResponseEntity.ok(horarioAtualizadoBD);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao atualizar o horário: " + ex.getMessage());
        }
    }

    @Operation(summary = "Excluir horário disponível", description = "Exclui um horário disponível específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado")
    })
    @DeleteMapping("/{horarioId}")
    public ResponseEntity<Void> deletarHorario(
            @Parameter(description = "ID do horário", required = true) @PathVariable UUID horarioId) {
        try {
            if (horarioDisponivelRepository.existsById(horarioId)) {
                horarioDisponivelRepository.deleteById(horarioId);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
