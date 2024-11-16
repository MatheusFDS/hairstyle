package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Servico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoPort {
    Optional<Servico> buscarPorId(UUID id);
    List<Servico> buscarTodos();
    Servico salvar(Servico servico);
    void deletar(UUID id); // Adicionado o método deletar
    boolean existePorId(UUID id); // Garante que existe o método de verificação
}
