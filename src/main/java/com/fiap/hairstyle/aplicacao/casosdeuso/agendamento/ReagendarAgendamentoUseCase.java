package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import com.fiap.hairstyle.dominio.portas.saida.NotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReagendarAgendamentoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReagendarAgendamentoUseCase.class);

    private final AgendamentoPort agendamentoPort;
    private final NotificacaoPort notificacaoPort;

    public ReagendarAgendamentoUseCase(AgendamentoPort agendamentoPort, NotificacaoPort notificacaoPort) {
        this.agendamentoPort = agendamentoPort;
        this.notificacaoPort = notificacaoPort;
    }

    public Agendamento executar(UUID agendamentoId, LocalDateTime novaDataHora) {
        Agendamento agendamento = agendamentoPort.buscarPorId(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado para o ID: " + agendamentoId));

        agendamento.setDataHora(novaDataHora);
        Agendamento atualizado = agendamentoPort.salvar(agendamento);

        try {
            notificacaoPort.enviarConfirmacao(atualizado);
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação de reagendamento para o agendamento ID: {}", agendamentoId, e);
        }

        return atualizado;
    }
}
