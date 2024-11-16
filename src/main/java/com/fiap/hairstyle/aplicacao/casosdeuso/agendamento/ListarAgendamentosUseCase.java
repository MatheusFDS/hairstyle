package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarAgendamentosUseCase {

    private final AgendamentoPort agendamentoPort;

    public ListarAgendamentosUseCase(AgendamentoPort agendamentoPort) {
        this.agendamentoPort = agendamentoPort;
    }

    public List<Agendamento> executar() {
        return agendamentoPort.buscarTodos();
    }
}
