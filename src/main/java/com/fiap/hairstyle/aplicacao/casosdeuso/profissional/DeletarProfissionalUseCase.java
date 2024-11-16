package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarProfissionalUseCase {

    private final ProfissionalPort profissionalPort;

    public DeletarProfissionalUseCase(ProfissionalPort profissionalPort) {
        this.profissionalPort = profissionalPort;
    }

    public boolean executar(UUID id) {
        if (profissionalPort.existePorId(id)) {
            profissionalPort.deletar(id);
            return true;
        }
        return false;
    }
}
