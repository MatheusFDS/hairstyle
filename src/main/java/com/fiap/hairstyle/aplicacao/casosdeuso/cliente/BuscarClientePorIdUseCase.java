package com.fiap.hairstyle.aplicacao.casosdeuso.cliente;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarClientePorIdUseCase {

    private final ClientePort clientePort;

    public BuscarClientePorIdUseCase(ClientePort clientePort) {
        this.clientePort = clientePort;
    }

    public Optional<Cliente> executar(UUID id) {
        return clientePort.buscarPorId(id);
    }
}
