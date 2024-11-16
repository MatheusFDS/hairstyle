package com.fiap.hairstyle.adaptadores.saida.adpter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.portas.saida.AgendamentoPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AgendamentoAdapter implements AgendamentoPort {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoAdapter(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    public Optional<Agendamento> buscarPorId(UUID id) {
        return agendamentoRepository.findById(id);
    }

    @Override
    public List<Agendamento> buscarTodos() {
        return agendamentoRepository.findAll();
    }

    @Override
    public Optional<Agendamento> buscarPorProfissionalEHorario(UUID profissionalId, LocalDateTime dataHora) {
        // Mapear o primeiro elemento da lista para Optional
        return agendamentoRepository.findByProfissionalAndDataHora(profissionalId, dataHora)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Agendamento> buscarPorClienteEHorario(UUID clienteId, LocalDateTime dataHora) {
        // Mapear o primeiro elemento da lista para Optional
        return agendamentoRepository.findByClienteAndDataHora(clienteId, dataHora)
                .stream()
                .findFirst();
    }

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    @Override
    public void deletar(Agendamento agendamento) {
        agendamentoRepository.delete(agendamento);
    }
}
