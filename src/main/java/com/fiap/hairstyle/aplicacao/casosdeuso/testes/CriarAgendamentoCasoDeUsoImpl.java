package com.fiap.hairstyle.aplicacao.casosdeuso.testes;

import com.fiap.hairstyle.dominio.portas.saida.AgendamentoSaidaPort;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.servico.NotificacaoService;

public class CriarAgendamentoCasoDeUsoImpl implements CriarAgendamentoCasoDeUso {

    private final AgendamentoSaidaPort agendamentoSaidaPort;
    private final NotificacaoService notificacaoService;

    public CriarAgendamentoCasoDeUsoImpl(AgendamentoSaidaPort agendamentoSaidaPort, NotificacaoService notificacaoService) {
        this.agendamentoSaidaPort = agendamentoSaidaPort;
        this.notificacaoService = notificacaoService;
    }

    @Override
    public Agendamento executar(Agendamento agendamento) {
        // Verifica se já existe um agendamento no horário especificado
        if (agendamentoSaidaPort.existeAgendamentoNoHorario(agendamento.getProfissional().getId(), agendamento.getDataHora())) {
            throw new RuntimeException("Horário indisponível para o profissional.");
        }

        // Salva o agendamento
        Agendamento agendamentoCriado = agendamentoSaidaPort.salvar(agendamento);

        // Envia notificação de confirmação
        notificacaoService.enviarConfirmacao(agendamentoCriado);

        return agendamentoCriado;
    }
}
