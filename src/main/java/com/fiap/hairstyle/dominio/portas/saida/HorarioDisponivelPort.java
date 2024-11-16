package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;

import java.util.List;
import java.util.UUID;

public interface HorarioDisponivelPort {
    List<HorarioDisponivel> buscarPorProfissionalId(UUID profissionalId);

    void salvarTodos(List<HorarioDisponivel> horarios); // Adiciona este método
}
