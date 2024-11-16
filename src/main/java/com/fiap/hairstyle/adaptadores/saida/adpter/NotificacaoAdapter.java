package com.fiap.hairstyle.adaptadores.saida.adpter;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.NotificacaoPort;
import com.fiap.hairstyle.dominio.servico.NotificacaoService;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoAdapter implements NotificacaoPort {

    private final NotificacaoService notificacaoService;

    public NotificacaoAdapter(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento) {
        notificacaoService.enviarConfirmacao(agendamento);
    }

    @Override
    public void enviarCancelamento(Agendamento agendamento) {
        notificacaoService.enviarCancelamento(agendamento);
    }

    @Override
    public boolean enviarNaoComparecimento(Agendamento agendamento) {
        try {
            notificacaoService.enviarNaoComparecimento(agendamento); // Chamando o serviço real
            return true; // Sucesso
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de não comparecimento: " + e.getMessage());
            return false; // Falha
        }
    }
}
