package com.fiap.hairstyle.adaptadores.saida.repositorios.custom;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;

import java.util.List;

public interface EstabelecimentoRepositoryCustom {
    List<Estabelecimento> filtrarEstabelecimentos(String nome, String endereco, Double precoMin, Double precoMax, String servico, Double avaliacaoMinima);
}
