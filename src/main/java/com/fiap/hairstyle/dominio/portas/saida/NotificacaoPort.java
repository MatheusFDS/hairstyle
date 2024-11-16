package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Agendamento;

public interface NotificacaoPort {
    void enviarConfirmacao(Agendamento agendamento);
    void enviarCancelamento(Agendamento agendamento);
    boolean enviarNaoComparecimento(Agendamento agendamento); // Retorna boolean agora
}
