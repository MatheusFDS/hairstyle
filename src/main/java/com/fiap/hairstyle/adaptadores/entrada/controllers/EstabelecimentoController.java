package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos")
@Tag(name = "Estabelecimentos", description = "Endpoints para gerenciamento de estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class EstabelecimentoController {

    private final ListarEstabelecimentosUseCase listarEstabelecimentosUseCase;
    private final BuscarEstabelecimentoPorIdUseCase buscarEstabelecimentoPorIdUseCase;
    private final CriarEstabelecimentoUseCase criarEstabelecimentoUseCase;
    private final AtualizarEstabelecimentoUseCase atualizarEstabelecimentoUseCase;
    private final DeletarEstabelecimentoUseCase deletarEstabelecimentoUseCase;
    private final FiltrarEstabelecimentosUseCase filtrarEstabelecimentosUseCase;

    public EstabelecimentoController(
            ListarEstabelecimentosUseCase listarEstabelecimentosUseCase,
            BuscarEstabelecimentoPorIdUseCase buscarEstabelecimentoPorIdUseCase,
            CriarEstabelecimentoUseCase criarEstabelecimentoUseCase,
            AtualizarEstabelecimentoUseCase atualizarEstabelecimentoUseCase,
            DeletarEstabelecimentoUseCase deletarEstabelecimentoUseCase,
            FiltrarEstabelecimentosUseCase filtrarEstabelecimentosUseCase
    ) {
        this.listarEstabelecimentosUseCase = listarEstabelecimentosUseCase;
        this.buscarEstabelecimentoPorIdUseCase = buscarEstabelecimentoPorIdUseCase;
        this.criarEstabelecimentoUseCase = criarEstabelecimentoUseCase;
        this.atualizarEstabelecimentoUseCase = atualizarEstabelecimentoUseCase;
        this.deletarEstabelecimentoUseCase = deletarEstabelecimentoUseCase;
        this.filtrarEstabelecimentosUseCase = filtrarEstabelecimentosUseCase;
    }

    @Operation(summary = "Listar todos os estabelecimentos", description = "Retorna uma lista de todos os estabelecimentos cadastrados.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")})
    @GetMapping
    public ResponseEntity<List<Estabelecimento>> listarTodos() {
        List<Estabelecimento> estabelecimentos = listarEstabelecimentosUseCase.executar();
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimento por ID", description = "Busca os detalhes de um estabelecimento específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento encontrado."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(
            @Parameter(description = "ID do estabelecimento a ser buscado", required = true) @PathVariable UUID id) {
        Optional<Estabelecimento> estabelecimento = buscarEstabelecimentoPorIdUseCase.executar(id);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo estabelecimento", description = "Cadastra um novo estabelecimento no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estabelecimento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o estabelecimento.")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Estabelecimento> criar(
            @Parameter(description = "Dados do estabelecimento a ser criado", required = true) @RequestBody Estabelecimento estabelecimento) {
        Estabelecimento novoEstabelecimento = criarEstabelecimentoUseCase.executar(estabelecimento);
        return ResponseEntity.status(201).body(novoEstabelecimento);
    }

    @Operation(summary = "Atualizar um estabelecimento", description = "Atualiza os dados de um estabelecimento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizar(
            @Parameter(description = "ID do estabelecimento a ser atualizado", required = true) @PathVariable UUID id,
            @Parameter(description = "Dados atualizados do estabelecimento", required = true) @RequestBody Estabelecimento estabelecimentoAtualizado) {
        Optional<Estabelecimento> estabelecimento = atualizarEstabelecimentoUseCase.executar(id, estabelecimentoAtualizado);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar um estabelecimento", description = "Exclui um estabelecimento do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estabelecimento deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do estabelecimento a ser deletado", required = true) @PathVariable UUID id) {
        boolean deletado = deletarEstabelecimentoUseCase.executar(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Filtrar estabelecimentos", description = "Busca estabelecimentos com base em filtros.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de estabelecimentos retornada com sucesso.")})
    @GetMapping("/filtros")
    public ResponseEntity<List<Estabelecimento>> filtrarEstabelecimentos(
            @Parameter(description = "Nome do estabelecimento") @RequestParam(required = false) String nome,
            @Parameter(description = "Endereço do estabelecimento") @RequestParam(required = false) String endereco,
            @Parameter(description = "Faixa mínima de preço") @RequestParam(required = false) Double precoMin,
            @Parameter(description = "Faixa máxima de preço") @RequestParam(required = false) Double precoMax,
            @Parameter(description = "Serviço oferecido") @RequestParam(required = false) String servico,
            @Parameter(description = "Nota mínima de avaliação") @RequestParam(required = false) Double avaliacaoMinima) {
        List<Estabelecimento> estabelecimentos = filtrarEstabelecimentosUseCase.executar(nome, endereco, precoMin, precoMax, servico, avaliacaoMinima);
        return ResponseEntity.ok(estabelecimentos);
    }
}
