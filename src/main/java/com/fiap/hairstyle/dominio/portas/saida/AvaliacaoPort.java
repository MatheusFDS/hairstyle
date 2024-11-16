package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Avaliacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoPort {
    Avaliacao salvar(Avaliacao avaliacao);
    Optional<Avaliacao> buscarPorId(UUID id);
    List<Avaliacao> buscarPorProfissional(UUID profissionalId);
    List<Avaliacao> buscarPorEstabelecimento(UUID estabelecimentoId);
    Double calcularMediaPorProfissional(UUID profissionalId);
    Double calcularMediaPorEstabelecimento(UUID estabelecimentoId);
    boolean existePorId(UUID id);
    void deletarPorId(UUID id); // Adicionado o método
}
