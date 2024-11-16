package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarAgendamentoPorIdUseCase {

    private final AgendamentoPort agendamentoPort;

    public BuscarAgendamentoPorIdUseCase(AgendamentoPort agendamentoPort) {
        this.agendamentoPort = agendamentoPort;
    }

    public Agendamento executar(UUID id) {
        return agendamentoPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
    }
}
