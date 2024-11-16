package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarAgendamentosPorEstabelecimentoUseCase {

    private final AgendamentoPort agendamentoPort;

    public ListarAgendamentosPorEstabelecimentoUseCase(AgendamentoPort agendamentoPort) {
        this.agendamentoPort = agendamentoPort;
    }

    public List<Agendamento> executar(UUID estabelecimentoId) {
        return agendamentoPort.buscarTodos()
                .stream()
                .filter(agendamento -> agendamento.getProfissional().getEstabelecimento().getId().equals(estabelecimentoId))
                .toList();
    }
}
