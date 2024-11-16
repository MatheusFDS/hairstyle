package com.fiap.hairstyle.aplicacao.casosdeuso.cliente;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClientesUseCase {

    private final ClientePort clientePort;

    public ListarClientesUseCase(ClientePort clientePort) {
        this.clientePort = clientePort;
    }

    public List<Cliente> executar() {
        return clientePort.buscarTodos();
    }
}
