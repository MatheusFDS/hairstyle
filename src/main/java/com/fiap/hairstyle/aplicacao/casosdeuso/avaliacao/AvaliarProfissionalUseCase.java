package com.fiap.hairstyle.aplicacao.casosdeuso.avaliacao;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import com.fiap.hairstyle.dominio.portas.saida.AvaliacaoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AvaliarProfissionalUseCase {

    private final AgendamentoPort agendamentoPort;
    private final AvaliacaoPort avaliacaoPort;

    public AvaliarProfissionalUseCase(AgendamentoPort agendamentoPort, AvaliacaoPort avaliacaoPort) {
        this.agendamentoPort = agendamentoPort;
        this.avaliacaoPort = avaliacaoPort;
    }

    public Avaliacao executar(UUID agendamentoId, Avaliacao avaliacao) {
        Agendamento agendamento = agendamentoPort.buscarPorId(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        // Associar o profissional e cliente ao objeto Avaliação
        avaliacao.setProfissional(agendamento.getProfissional());
        avaliacao.setCliente(agendamento.getCliente());

        return avaliacaoPort.salvar(avaliacao);
    }
}
