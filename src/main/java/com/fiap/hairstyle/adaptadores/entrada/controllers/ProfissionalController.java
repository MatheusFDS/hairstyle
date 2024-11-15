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
@RequestMapping("/api/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para gerenciamento de profissionais e disponibilidade")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Operation(summary = "Listar todos os profissionais", description = "Retorna uma lista de todos os profissionais cadastrados.")
    @GetMapping
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    @Operation(summary = "Buscar profissional por ID", description = "Busca um profissional específico pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        return profissional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo profissional", description = "Cadastra um novo profissional no sistema.")
    @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso")
    @PostMapping
    public Profissional criar(@RequestBody Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    @Operation(summary = "Atualizar profissional", description = "Atualiza as informações de um profissional específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id,
            @RequestBody Profissional profissionalAtualizado) {
        return profissionalRepository.findById(id).map(profissional -> {
            profissional.setNome(profissionalAtualizado.getNome());
            profissional.setEspecialidade(profissionalAtualizado.getEspecialidade());
            profissional.setTelefone(profissionalAtualizado.getTelefone());
            profissional.setTarifa(profissionalAtualizado.getTarifa());
            profissional.setEstabelecimento(profissionalAtualizado.getEstabelecimento());
            return ResponseEntity.ok(profissionalRepository.save(profissional));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar profissional", description = "Exclui um profissional específico do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profissional excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id) {
        if (profissionalRepository.existsById(id)) {
            profissionalRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Definir disponibilidade de horários", description = "Define a disponibilidade de horários para um profissional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidade definida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @PostMapping("/{id}/disponibilidade")
    public ResponseEntity<?> definirDisponibilidade(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id,
            @RequestBody List<HorarioDisponivel> horarios) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        if (profissional.isPresent()) {
            horarios.forEach(horario -> horario.setProfissional(profissional.get()));
            horarioDisponivelRepository.saveAll(horarios);
            return ResponseEntity.ok("Disponibilidade definida com sucesso.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar disponibilidade de horários", description = "Retorna a disponibilidade de horários de um profissional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<List<HorarioDisponivel>> listarDisponibilidade(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id) {
        List<HorarioDisponivel> disponibilidade = horarioDisponivelRepository.findByProfissionalId(id);
        return ResponseEntity.ok(disponibilidade);
    }
}
