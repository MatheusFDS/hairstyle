package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CriarEstabelecimentoUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public CriarEstabelecimentoUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public Estabelecimento executar(Estabelecimento estabelecimento) {
        estabelecimento.setProfissionais(List.of());
        estabelecimento.setServicos(List.of());
        estabelecimento.setAvaliacoes(List.of());
        return estabelecimentoPort.salvar(estabelecimento);
    }
}
