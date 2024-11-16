package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.aplicacao.casosdeuso.agendamento.BuscarAgendamentoPorIdUseCase;
import com.fiap.hairstyle.aplicacao.casosdeuso.agendamento.CancelarAgendamentoUseCase;
import com.fiap.hairstyle.aplicacao.casosdeuso.agendamento.CriarAgendamentoUseCase;
import com.fiap.hairstyle.aplicacao.casosdeuso.agendamento.ListarAgendamentosUseCase;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Endpoints para gerenciamento de agendamentos de serviços")
@SecurityRequirement(name = "Bearer Authentication")
public class AgendamentoController {

    private final CriarAgendamentoUseCase criarAgendamentoUseCase;
    private final ListarAgendamentosUseCase listarAgendamentosUseCase;
    private final BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase;
    private final CancelarAgendamentoUseCase cancelarAgendamentoUseCase;

    public AgendamentoController(CriarAgendamentoUseCase criarAgendamentoUseCase,
                                 ListarAgendamentosUseCase listarAgendamentosUseCase,
                                 BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase,
                                 CancelarAgendamentoUseCase cancelarAgendamentoUseCase) {
        this.criarAgendamentoUseCase = criarAgendamentoUseCase;
        this.listarAgendamentosUseCase = listarAgendamentosUseCase;
        this.buscarAgendamentoPorIdUseCase = buscarAgendamentoPorIdUseCase;
        this.cancelarAgendamentoUseCase = cancelarAgendamentoUseCase;
    }

    @Operation(
            summary = "Listar todos os agendamentos",
            description = "Retorna uma lista de todos os agendamentos cadastrados no sistema. Requer autenticação via token JWT."
    )
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso.")
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        return ResponseEntity.ok(listarAgendamentosUseCase.executar());
    }

    @Operation(
            summary = "Buscar agendamento por ID",
            description = "Busca os detalhes de um agendamento específico pelo ID. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id) {
        try {
            return ResponseEntity.ok(buscarAgendamentoPorIdUseCase.executar(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Criar novo agendamento",
            description = "Cria um novo agendamento com os detalhes fornecidos. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro nos dados do agendamento.")
    })
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) {
        try {
            return ResponseEntity.status(201).body(criarAgendamentoUseCase.executar(agendamento));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Cancelar agendamento",
            description = "Cancela um agendamento pelo ID fornecido. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @Parameter(description = "ID do agendamento a ser cancelado", required = true) @PathVariable UUID id) {
        try {
            cancelarAgendamentoUseCase.executar(id);
            return ResponseEntity.ok("Agendamento cancelado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
