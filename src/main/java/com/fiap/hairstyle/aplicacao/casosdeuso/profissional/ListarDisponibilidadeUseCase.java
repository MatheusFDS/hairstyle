package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.portas.saida.HorarioDisponivelPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarDisponibilidadeUseCase {

    private final HorarioDisponivelPort horarioDisponivelPort;

    public ListarDisponibilidadeUseCase(HorarioDisponivelPort horarioDisponivelPort) {
        this.horarioDisponivelPort = horarioDisponivelPort;
    }

    public List<HorarioDisponivel> executar(UUID profissionalId) {
        return horarioDisponivelPort.buscarPorProfissionalId(profissionalId);
    }
}
