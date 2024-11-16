package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Servico;

import java.util.Optional;
import java.util.UUID;

public interface ServicoPort {
    Optional<Servico> buscarPorId(UUID id);
}
