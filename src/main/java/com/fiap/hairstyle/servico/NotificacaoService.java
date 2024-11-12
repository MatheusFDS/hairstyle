package com.fiap.hairstyle.servico;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    public void enviarConfirmacao(Agendamento agendamento) {
        // Lógica para enviar confirmação (pode ser via e-mail, SMS, etc.)
        System.out.println("Confirmação enviada para o cliente " + agendamento.getCliente().getNome() +
                " e o profissional " + agendamento.getProfissional().getNome() +
                " para o horário " + agendamento.getDataHora());
    }

    public void enviarLembrete(Agendamento agendamento) {
        // Lógica para enviar lembrete
        System.out.println("Lembrete enviado para o cliente " + agendamento.getCliente().getNome() +
                " e o profissional " + agendamento.getProfissional().getNome() +
                " para o horário " + agendamento.getDataHora());
    }
}
