package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.servico.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos/{estabelecimentoId}/painel")
@Tag(name = "Painel do Estabelecimento", description = "Operações de gerenciamento de agendamentos para estabelecimentos")
public class EstabelecimentoPainelController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Operation(summary = "Listar agendamentos", description = "Retorna todos os agendamentos de um estabelecimento específico.")
    @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso")
    @GetMapping("/agendamentos")
    public ResponseEntity<List<Agendamento>> listarAgendamentos(
            @Parameter(description = "ID do estabelecimento", required = true) @PathVariable UUID estabelecimentoId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByEstabelecimentoId(estabelecimentoId);
        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Cancelar agendamento", description = "Permite o cancelamento de um agendamento específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/agendamentos/{agendamentoId}")
    public ResponseEntity<Void> cancelarAgendamento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID agendamentoId) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(agendamentoId);
        if (agendamento.isPresent()) {
            agendamentoRepository.deleteById(agendamentoId);
            notificacaoService.enviarCancelamento(agendamento.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Reagendar agendamento", description = "Permite reagendar um agendamento para uma nova data e hora.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento reagendado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping("/agendamentos/{agendamentoId}/reagendar")
    public ResponseEntity<Agendamento> reagendarAgendamento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID agendamentoId,
            @Parameter(description = "Nova data e hora para o agendamento", required = true) @RequestBody LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setDataHora(novaDataHora);
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarConfirmacao(agendamento);
            return ResponseEntity.ok(agendamento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Marcar não comparecimento", description = "Marca um agendamento como 'não comparecimento'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento marcado como não comparecimento com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping("/agendamentos/{agendamentoId}/naoComparecimento")
    public ResponseEntity<Agendamento> marcarNaoComparecimento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID agendamentoId) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setNaoComparecimento(true);
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarNaoComparecimento(agendamento);
            return ResponseEntity.ok(agendamento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
