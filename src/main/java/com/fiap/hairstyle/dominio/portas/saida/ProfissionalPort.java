package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Profissional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfissionalPort {
    List<Profissional> buscarTodos();
    Optional<Profissional> buscarPorId(UUID id);
    Profissional salvar(Profissional profissional);
    void deletar(UUID id);
    boolean existePorId(UUID id);
}
