package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

@Service
public class CriarProfissionalUseCase {

    private final ProfissionalPort profissionalPort;

    public CriarProfissionalUseCase(ProfissionalPort profissionalPort) {
        this.profissionalPort = profissionalPort;
    }

    public Profissional executar(Profissional profissional) {
        return profissionalPort.salvar(profissional);
    }
}
