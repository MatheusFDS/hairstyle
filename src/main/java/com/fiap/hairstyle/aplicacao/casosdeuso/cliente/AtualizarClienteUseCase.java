package com.fiap.hairstyle.aplicacao.casosdeuso.cliente;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarClienteUseCase {

    private final ClientePort clientePort;

    public AtualizarClienteUseCase(ClientePort clientePort) {
        this.clientePort = clientePort;
    }

    public Optional<Cliente> executar(UUID id, Cliente clienteAtualizado) {
        return clientePort.buscarPorId(id).map(cliente -> {
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setTelefone(clienteAtualizado.getTelefone());
            cliente.setEmail(clienteAtualizado.getEmail());
            return clientePort.salvar(cliente);
        });
    }
}
