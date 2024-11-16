package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.adaptadores.saida.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.ProfissionalRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.EstabelecimentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para gerenciamento de profissionais e disponibilidade")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfissionalController {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Operation(summary = "Listar todos os profissionais",
            description = "Retorna uma lista de todos os profissionais cadastrados. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de profissionais retornada com sucesso.")
    })
    @GetMapping(produces = "application/json")
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    @Operation(summary = "Buscar profissional por ID",
            description = "Busca um profissional específico pelo ID. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Profissional> buscarPorId(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        return profissional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo profissional",
            description = "Cadastra um novo profissional no sistema. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado.")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> criar(
            @Parameter(description = "Dados do profissional a ser criado", required = true) @RequestBody Profissional profissional) {
        if (profissional.getEstabelecimento() == null || profissional.getEstabelecimento().getId() == null) {
            return ResponseEntity.badRequest().body("Estabelecimento é obrigatório.");
        }

        Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findById(profissional.getEstabelecimento().getId());
        if (!estabelecimentoOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Estabelecimento não encontrado.");
        }

        profissional.setEstabelecimento(estabelecimentoOpt.get());
        Profissional novoProfissional = profissionalRepository.save(profissional);
        return ResponseEntity.status(201).body(novoProfissional);
    }

    @Operation(summary = "Atualizar profissional",
            description = "Atualiza as informações de um profissional existente. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> atualizar(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id,
            @Parameter(description = "Dados do profissional a ser atualizado", required = true) @RequestBody Profissional profissionalAtualizado) {
        return profissionalRepository.findById(id).map(profissional -> {
            profissional.setNome(profissionalAtualizado.getNome());
            profissional.setEspecialidade(profissionalAtualizado.getEspecialidade());
            profissional.setTelefone(profissionalAtualizado.getTelefone());
            profissional.setTarifa(profissionalAtualizado.getTarifa());

            if (profissionalAtualizado.getEstabelecimento() != null && profissionalAtualizado.getEstabelecimento().getId() != null) {
                Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findById(profissionalAtualizado.getEstabelecimento().getId());
                if (!estabelecimentoOpt.isPresent()) {
                    return ResponseEntity.badRequest().body("Estabelecimento não encontrado.");
                }
                profissional.setEstabelecimento(estabelecimentoOpt.get());
            } else {
                return ResponseEntity.badRequest().body("Estabelecimento é obrigatório.");
            }

            Profissional atualizado = profissionalRepository.save(profissional);
            return ResponseEntity.ok(atualizado);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Definir disponibilidade de horários",
            description = "Define a disponibilidade de horários para um profissional. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidade definida com sucesso."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    @PostMapping("/{id}/disponibilidade")
    public ResponseEntity<?> definirDisponibilidade(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id,
            @Parameter(description = "Lista de horários disponíveis para o profissional", required = true) @RequestBody List<HorarioDisponivel> horarios) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
        if (profissional.isPresent()) {
            horarios.forEach(horario -> horario.setProfissional(profissional.get()));
            horarioDisponivelRepository.saveAll(horarios);
            return ResponseEntity.ok("Disponibilidade definida com sucesso.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar disponibilidade de horários",
            description = "Retorna a disponibilidade de horários de um profissional. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidade retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<List<HorarioDisponivel>> listarDisponibilidade(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID id) {
        List<HorarioDisponivel> disponibilidade = horarioDisponivelRepository.findByProfissionalId(id);
        return ResponseEntity.ok(disponibilidade);
    }
}
