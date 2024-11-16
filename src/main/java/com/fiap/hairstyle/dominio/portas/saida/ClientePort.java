package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientePort {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(UUID id);
    List<Cliente> buscarTodos();
    void deletarPorId(UUID id);
    boolean existePorId(UUID id);
}
