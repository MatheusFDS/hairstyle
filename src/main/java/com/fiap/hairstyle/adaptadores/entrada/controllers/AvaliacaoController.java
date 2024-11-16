package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.adaptadores.saida.repositorios.AvaliacaoRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "Endpoints para avaliar profissionais e estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

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
            @Valid @RequestBody Avaliacao avaliacao) {

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);

        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!agendamento.isNaoComparecimento()) {
            return ResponseEntity.badRequest().body("Agendamento precisa estar concluído para avaliação.");
        }

        Profissional profissional = agendamento.getProfissional();
        avaliacao.setProfissional(profissional);
        avaliacao.setCliente(agendamento.getCliente());

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
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
            @Valid @RequestBody Avaliacao avaliacao) {

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);

        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!agendamento.isNaoComparecimento()) {
            return ResponseEntity.badRequest().body("Agendamento precisa estar concluído para avaliação.");
        }

        Estabelecimento estabelecimento = agendamento.getProfissional().getEstabelecimento();
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setCliente(agendamento.getCliente());

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    @Operation(
            summary = "Listar avaliações de um profissional",
            description = "Retorna uma lista de todas as avaliações de um profissional específico. Requer autenticação via token JWT."
    )
    @ApiResponse(responseCode = "200", description = "Avaliações do profissional retornadas com sucesso.")
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesProfissional(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByProfissionalId(profissionalId);
        return ResponseEntity.ok(avaliacoes);
    }

    @Operation(
            summary = "Listar avaliações de um estabelecimento",
            description = "Retorna uma lista de todas as avaliações de um estabelecimento específico. Requer autenticação via token JWT."
    )
    @ApiResponse(responseCode = "200", description = "Avaliações do estabelecimento retornadas com sucesso.")
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesEstabelecimento(
            @Parameter(description = "ID do estabelecimento", required = true) @PathVariable UUID estabelecimentoId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimentoId);
        return ResponseEntity.ok(avaliacoes);
    }
}
