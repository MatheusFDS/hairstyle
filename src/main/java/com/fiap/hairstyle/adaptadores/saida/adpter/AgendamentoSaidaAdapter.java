package com.fiap.hairstyle.adaptadores.saida.adpter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoSaidaPort;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
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
