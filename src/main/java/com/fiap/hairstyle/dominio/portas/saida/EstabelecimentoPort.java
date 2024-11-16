package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstabelecimentoPort {
    Estabelecimento salvar(Estabelecimento estabelecimento);
    Optional<Estabelecimento> buscarPorId(UUID id);
    List<Estabelecimento> buscarTodos();
    void deletarPorId(UUID id);
    boolean existePorId(UUID id);
    List<Estabelecimento> filtrar(String nome, String endereco, Double precoMin, Double precoMax, String servico, Double avaliacaoMinima);
}
