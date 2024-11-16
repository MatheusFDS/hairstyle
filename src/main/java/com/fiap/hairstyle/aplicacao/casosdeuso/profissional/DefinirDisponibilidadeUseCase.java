package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.HorarioDisponivelPort;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DefinirDisponibilidadeUseCase {

    private final HorarioDisponivelPort horarioDisponivelPort;
    private final ProfissionalPort profissionalPort;

    public DefinirDisponibilidadeUseCase(HorarioDisponivelPort horarioDisponivelPort, ProfissionalPort profissionalPort) {
        this.horarioDisponivelPort = horarioDisponivelPort;
        this.profissionalPort = profissionalPort;
    }

    public boolean executar(UUID profissionalId, List<HorarioDisponivel> horarios) {
        // Verifica se o profissional existe
        Profissional profissional = profissionalPort.buscarPorId(profissionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado."));

        // Atribui o profissional a cada horário
        horarios.forEach(horario -> horario.setProfissional(profissional));

        // Salva todos os horários no repositório
        horarioDisponivelPort.salvarTodos(horarios);
        return true;
    }
}
