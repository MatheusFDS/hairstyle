package com.fiap.hairstyle.aplicacao.casosdeuso.cliente;

import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarClienteUseCase {

    private final ClientePort clientePort;

    public DeletarClienteUseCase(ClientePort clientePort) {
        this.clientePort = clientePort;
    }

    public boolean executar(UUID id) {
        if (clientePort.existePorId(id)) {
            clientePort.deletarPorId(id);
            return true;
        }
        return false;
    }
}
