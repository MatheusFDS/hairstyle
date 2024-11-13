package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.servico.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos/{estabelecimentoId}/painel")
public class EstabelecimentoPainelController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    // Listar agendamentos de um estabelecimento
    @GetMapping("/agendamentos")
    public ResponseEntity<List<Agendamento>> listarAgendamentos(@PathVariable UUID estabelecimentoId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByEstabelecimentoId(estabelecimentoId);
        return ResponseEntity.ok(agendamentos);
    }

    // Cancelar um agendamento
    @DeleteMapping("/agendamentos/{agendamentoId}")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable UUID agendamentoId) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(agendamentoId);
        if (agendamento.isPresent()) {
            agendamentoRepository.deleteById(agendamentoId);
            notificacaoService.enviarCancelamento(agendamento.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Reagendar um agendamento
    @PutMapping("/agendamentos/{agendamentoId}/reagendar")
    public ResponseEntity<Agendamento> reagendarAgendamento(
            @PathVariable UUID agendamentoId,
            @RequestBody LocalDateTime novaDataHora) {
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

    // Marcar um agendamento como "não comparecimento"
    @PutMapping("/agendamentos/{agendamentoId}/naoComparecimento")
    public ResponseEntity<Agendamento> marcarNaoComparecimento(@PathVariable UUID agendamentoId) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setNaoComparecimento(true); // Precisa do campo "naoComparecimento" em Agendamento
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarNaoComparecimento(agendamento); // Notificação opcional
            return ResponseEntity.ok(agendamento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
