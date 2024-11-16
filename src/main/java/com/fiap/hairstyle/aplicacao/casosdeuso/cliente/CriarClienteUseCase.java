package com.fiap.hairstyle.aplicacao.casosdeuso.cliente;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Service;

@Service
public class CriarClienteUseCase {

    private final ClientePort clientePort;

    public CriarClienteUseCase(ClientePort clientePort) {
        this.clientePort = clientePort;
    }

    public Cliente executar(Cliente cliente) {
        return clientePort.salvar(cliente);
    }
}
