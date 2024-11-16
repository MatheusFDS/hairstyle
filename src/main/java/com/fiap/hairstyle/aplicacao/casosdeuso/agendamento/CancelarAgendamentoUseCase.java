package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import com.fiap.hairstyle.dominio.portas.saida.GoogleCalendarPort;
import com.fiap.hairstyle.dominio.portas.saida.NotificacaoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CancelarAgendamentoUseCase {

    private final AgendamentoPort agendamentoPort;
    private final GoogleCalendarPort googleCalendarPort;
    private final NotificacaoPort notificacaoPort;

    public CancelarAgendamentoUseCase(AgendamentoPort agendamentoPort,
                                      GoogleCalendarPort googleCalendarPort,
                                      NotificacaoPort notificacaoPort) {
        this.agendamentoPort = agendamentoPort;
        this.googleCalendarPort = googleCalendarPort;
        this.notificacaoPort = notificacaoPort;
    }

    public boolean executar(UUID id) {
        Agendamento agendamento = agendamentoPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        // Tentar deletar o evento no Google Calendar (caso esteja habilitado)
        try {
            if (googleCalendarPort.isEnabled()) {
                googleCalendarPort.deletarEvento(agendamento.getGoogleCalendarEventId());
            }
        } catch (Exception e) {
            // Apenas logar o erro e continuar
            System.err.println("Erro ao integrar com o Google Calendar: " + e.getMessage());
        }

        // Deletar o agendamento e enviar notificação
        agendamentoPort.deletar(agendamento);
        notificacaoPort.enviarCancelamento(agendamento);
        return false;
    }
}
