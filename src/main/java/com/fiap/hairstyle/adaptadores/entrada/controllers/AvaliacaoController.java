package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.aplicacao.casosdeuso.avaliacao.*;
import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "Endpoints para avaliar profissionais e estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class AvaliacaoController {

    private final AvaliarProfissionalUseCase avaliarProfissionalUseCase;
    private final AvaliarEstabelecimentoUseCase avaliarEstabelecimentoUseCase;
    private final AtualizarAvaliacaoUseCase atualizarAvaliacaoUseCase;
    private final DeletarAvaliacaoUseCase deletarAvaliacaoUseCase;

    public AvaliacaoController(AvaliarProfissionalUseCase avaliarProfissionalUseCase,
                               AvaliarEstabelecimentoUseCase avaliarEstabelecimentoUseCase,
                               AtualizarAvaliacaoUseCase atualizarAvaliacaoUseCase,
                               DeletarAvaliacaoUseCase deletarAvaliacaoUseCase) {
        this.avaliarProfissionalUseCase = avaliarProfissionalUseCase;
        this.avaliarEstabelecimentoUseCase = avaliarEstabelecimentoUseCase;
        this.atualizarAvaliacaoUseCase = atualizarAvaliacaoUseCase;
        this.deletarAvaliacaoUseCase = deletarAvaliacaoUseCase;
    }

    @Operation(
            summary = "Avaliar profissional",
            description = "Permite ao cliente avaliar um profissional após o agendamento. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação do profissional realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Agendamento não encontrado ou inválido para avaliação.")
    })
    @PostMapping("/profissional/{agendamentoId}")
    public ResponseEntity<?> avaliarProfissional(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID agendamentoId,
            @RequestBody Avaliacao avaliacao) {
        Avaliacao novaAvaliacao = avaliarProfissionalUseCase.executar(agendamentoId, avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    @Operation(
            summary = "Avaliar estabelecimento",
            description = "Permite ao cliente avaliar o estabelecimento após o agendamento. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação do estabelecimento realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Agendamento não encontrado ou inválido para avaliação.")
    })
    @PostMapping("/estabelecimento/{agendamentoId}")
    public ResponseEntity<?> avaliarEstabelecimento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID agendamentoId,
            @RequestBody Avaliacao avaliacao) {
        Avaliacao novaAvaliacao = avaliarEstabelecimentoUseCase.executar(agendamentoId, avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    @Operation(
            summary = "Atualizar uma avaliação",
            description = "Permite atualizar os detalhes de uma avaliação existente. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada.")
    })
    @PutMapping("/{avaliacaoId}")
    public ResponseEntity<?> atualizarAvaliacao(
            @Parameter(description = "ID da avaliação", required = true) @PathVariable UUID avaliacaoId,
            @RequestBody Avaliacao avaliacaoAtualizada) {
        Avaliacao avaliacao = atualizarAvaliacaoUseCase.executar(avaliacaoId, avaliacaoAtualizada);
        return ResponseEntity.ok(avaliacao);
    }

    @Operation(
            summary = "Deletar uma avaliação",
            description = "Exclui uma avaliação do sistema. Requer autenticação via token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Avaliação deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada.")
    })
    @DeleteMapping("/{avaliacaoId}")
    public ResponseEntity<Void> deletarAvaliacao(
            @Parameter(description = "ID da avaliação", required = true) @PathVariable UUID avaliacaoId) {
        deletarAvaliacaoUseCase.executar(avaliacaoId);
        return ResponseEntity.noContent().build();
    }
}
