package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.portas.saida.HorarioDisponivelPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HorarioDisponivelAdapter implements HorarioDisponivelPort {

    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public HorarioDisponivelAdapter(HorarioDisponivelRepository horarioDisponivelRepository) {
        this.horarioDisponivelRepository = horarioDisponivelRepository;
    }

    @Override
    public List<HorarioDisponivel> buscarPorProfissionalId(UUID profissionalId) {
        return horarioDisponivelRepository.findByProfissionalId(profissionalId);
    }

    @Override
    public void salvarTodos(List<HorarioDisponivel> horarios) {
        horarioDisponivelRepository.saveAll(horarios);
    }
}
