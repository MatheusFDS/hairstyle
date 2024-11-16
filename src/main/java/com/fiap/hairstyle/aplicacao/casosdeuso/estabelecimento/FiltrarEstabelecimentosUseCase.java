package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiltrarEstabelecimentosUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public FiltrarEstabelecimentosUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public List<Estabelecimento> executar(String nome, String endereco, Double precoMin, Double precoMax, String servico, Double avaliacaoMinima) {
        return estabelecimentoPort.filtrar(nome, endereco, precoMin, precoMax, servico, avaliacaoMinima);
    }
}
