package com.fiap.hairstyle.servico;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.entidades.Servico;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    public void enviarConfirmacao(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        Profissional profissional = agendamento.getProfissional();
        Servico servico = agendamento.getServico();

        // Validação para evitar NullPointerException
        String estabelecimentoNome = (profissional != null && profissional.getEstabelecimento() != null)
                ? profissional.getEstabelecimento().getNome()
                : "Estabelecimento não definido";

        System.out.println("Notificação de Agendamento Confirmado:");
        System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "Desconhecido"));
        System.out.println("Serviço: " + (servico != null ? servico.getNome() : "Desconhecido"));
        System.out.println("Profissional: " + (profissional != null ? profissional.getNome() : "Desconhecido"));
        System.out.println("Estabelecimento: " + estabelecimentoNome);
        System.out.println("Data e Hora: " + agendamento.getDataHora());
    }

    public void enviarCancelamento(Agendamento agendamento) {
        System.out.println("Notificação de Agendamento Cancelado:");
        System.out.println("Cliente: " + agendamento.getCliente().getNome());
        System.out.println("Serviço: " + agendamento.getServico().getNome());
        System.out.println("Profissional: " + agendamento.getProfissional().getNome());
        System.out.println("Estabelecimento: " + agendamento.getProfissional().getEstabelecimento().getNome());
        System.out.println("Data e Hora: " + agendamento.getDataHora());
    }


    public void enviarLembrete(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        Profissional profissional = agendamento.getProfissional();
        Servico servico = agendamento.getServico();

        String estabelecimentoNome = (profissional != null && profissional.getEstabelecimento() != null)
                ? profissional.getEstabelecimento().getNome()
                : "Estabelecimento não definido";

        System.out.println("Lembrete de Agendamento:");
        System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "Desconhecido"));
        System.out.println("Serviço: " + (servico != null ? servico.getNome() : "Desconhecido"));
        System.out.println("Profissional: " + (profissional != null ? profissional.getNome() : "Desconhecido"));
        System.out.println("Estabelecimento: " + estabelecimentoNome);
        System.out.println("Data e Hora: " + agendamento.getDataHora());
        System.out.println("Lembrete: Este é um lembrete para o seu próximo agendamento.");
    }
}
