package com.fiap.hairstyle.adaptadores.saida.repositorios;

import com.fiap.hairstyle.aplicacao.portas.saida.AgendamentoSaidaPort;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AgendamentoSaidaAdapter implements AgendamentoSaidaPort {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoSaidaAdapter(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    @Override
    public boolean existeAgendamentoNoHorario(UUID profissionalId, LocalDateTime dataHora) {
        return agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
    }
}
