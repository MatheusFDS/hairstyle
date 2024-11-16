package com.fiap.hairstyle.aplicacao.casosdeuso.servico;

import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarServicosUseCase {

    private final ServicoPort servicoPort;

    public ListarServicosUseCase(ServicoPort servicoPort) {
        this.servicoPort = servicoPort;
    }

    public List<Servico> executar() {
        return servicoPort.buscarTodos();
    }
}
