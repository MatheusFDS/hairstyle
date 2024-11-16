package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.aplicacao.casosdeuso.servico.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços oferecidos pelos estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicoController {

    private final CriarServicoUseCase criarServicoUseCase;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final DeletarServicoUseCase deletarServicoUseCase;
    private final ListarServicosUseCase listarServicosUseCase;

    public ServicoController(
            CriarServicoUseCase criarServicoUseCase,
            AtualizarServicoUseCase atualizarServicoUseCase,
            DeletarServicoUseCase deletarServicoUseCase,
            ListarServicosUseCase listarServicosUseCase) {
        this.criarServicoUseCase = criarServicoUseCase;
        this.atualizarServicoUseCase = atualizarServicoUseCase;
        this.deletarServicoUseCase = deletarServicoUseCase;
        this.listarServicosUseCase = listarServicosUseCase;
    }

    @Operation(summary = "Listar todos os serviços",
            description = "Retorna uma lista de todos os serviços disponíveis. Requer autenticação via token JWT.")
    @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso.")
    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(listarServicosUseCase.executar());
    }

    @Operation(summary = "Buscar serviço por ID",
            description = "Busca um serviço específico pelo ID. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id) {
        return listarServicosUseCase.executar().stream()
                .filter(servico -> servico.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo serviço",
            description = "Cadastra um novo serviço no sistema. O estabelecimento é obrigatório. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado.")
    })
    @PostMapping
    public ResponseEntity<Servico> criar(
            @Parameter(description = "Dados do serviço a ser criado", required = true) @Valid @RequestBody Servico servico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarServicoUseCase.executar(servico));
    }

    @Operation(summary = "Atualizar serviço",
            description = "Atualiza as informações de um serviço específico. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id,
            @Parameter(description = "Dados do serviço a ser atualizado", required = true) @Valid @RequestBody Servico servicoAtualizado) {
        return ResponseEntity.ok(atualizarServicoUseCase.executar(id, servicoAtualizado));
    }

    @Operation(summary = "Deletar serviço",
            description = "Exclui um serviço específico do sistema. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id) {
        deletarServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
