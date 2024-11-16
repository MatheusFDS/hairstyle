package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarProfissionalPorIdUseCase {

    private final ProfissionalPort profissionalPort;

    public BuscarProfissionalPorIdUseCase(ProfissionalPort profissionalPort) {
        this.profissionalPort = profissionalPort;
    }

    public Optional<Profissional> executar(UUID id) {
        return profissionalPort.buscarPorId(id);
    }
}
