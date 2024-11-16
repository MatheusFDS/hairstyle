package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.ClienteRepository;
import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.portas.saida.ClientePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClienteAdapter implements ClientePort {

    private final ClienteRepository clienteRepository;

    public ClienteAdapter(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public void deletarPorId(UUID id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(UUID id) {
        return clienteRepository.existsById(id);
    }
}
