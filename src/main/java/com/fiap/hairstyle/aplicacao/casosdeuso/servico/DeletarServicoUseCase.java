package com.fiap.hairstyle.aplicacao.casosdeuso.servico;

import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarServicoUseCase {

    private final ServicoPort servicoPort;

    public DeletarServicoUseCase(ServicoPort servicoPort) {
        this.servicoPort = servicoPort;
    }

    public void executar(UUID id) {
        if (!servicoPort.existePorId(id)) {
            throw new IllegalArgumentException("Serviço não encontrado.");
        }
        servicoPort.deletar(id);
    }
}
