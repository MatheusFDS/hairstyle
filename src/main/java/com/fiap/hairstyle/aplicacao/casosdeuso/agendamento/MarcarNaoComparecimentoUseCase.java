package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import com.fiap.hairstyle.dominio.portas.saida.NotificacaoPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MarcarNaoComparecimentoUseCase {

    private final AgendamentoPort agendamentoPort;
    private final NotificacaoPort notificacaoPort;

    public MarcarNaoComparecimentoUseCase(AgendamentoPort agendamentoPort, NotificacaoPort notificacaoPort) {
        this.agendamentoPort = agendamentoPort;
        this.notificacaoPort = notificacaoPort;
    }

    public Agendamento executar(UUID agendamentoId) {
        Optional<Agendamento> agendamentoOpt = agendamentoPort.buscarPorId(agendamentoId);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setNaoComparecimento(true);
            agendamentoPort.salvar(agendamento);
            boolean sucesso = notificacaoPort.enviarNaoComparecimento(agendamento);
            if (!sucesso) {
                throw new IllegalStateException("Erro ao enviar notificação de não comparecimento.");
            }
            return agendamento;
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado.");
        }
    }
}
