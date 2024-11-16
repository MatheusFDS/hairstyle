package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarProfissionaisUseCase {

    private final ProfissionalPort profissionalPort;

    public ListarProfissionaisUseCase(ProfissionalPort profissionalPort) {
        this.profissionalPort = profissionalPort;
    }

    public List<Profissional> executar() {
        return profissionalPort.buscarTodos();
    }
}
