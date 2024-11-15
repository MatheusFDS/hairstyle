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
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<HorarioDisponivel>> listarPorProfissional(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId) {
        List<HorarioDisponivel> horarios = horarioDisponivelRepository.findByProfissionalId(profissionalId);
        return ResponseEntity.ok(horarios);
    }

    @Operation(summary = "Criar horário disponível", description = "Cria um novo horário disponível para o profissional especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horário disponível criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @PostMapping("/profissional/{profissionalId}")
    public ResponseEntity<HorarioDisponivel> criarHorario(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId,
            @RequestBody HorarioDisponivel horario) {
        Optional<Profissional> profissionalOpt = profissionalRepository.findById(profissionalId);
        if (profissionalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        horario.setProfissional(profissionalOpt.get());
        HorarioDisponivel novoHorario = horarioDisponivelRepository.save(horario);
        return ResponseEntity.ok(novoHorario);
    }

    @Operation(summary = "Atualizar horário disponível", description = "Atualiza um horário disponível específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado")
    })
    @PutMapping("/{horarioId}")
    public ResponseEntity<HorarioDisponivel> atualizarHorario(
            @Parameter(description = "ID do horário", required = true) @PathVariable UUID horarioId,
            @RequestBody HorarioDisponivel horarioAtualizado) {
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

    @Operation(summary = "Excluir horário disponível", description = "Exclui um horário disponível específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Horário não encontrado")
    })
    @DeleteMapping("/{horarioId}")
    public ResponseEntity<Void> deletarHorario(
            @Parameter(description = "ID do horário", required = true) @PathVariable UUID horarioId) {
        if (horarioDisponivelRepository.existsById(horarioId)) {
            horarioDisponivelRepository.deleteById(horarioId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
