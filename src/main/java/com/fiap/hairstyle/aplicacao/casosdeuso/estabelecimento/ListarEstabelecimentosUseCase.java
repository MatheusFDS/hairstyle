package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarEstabelecimentosUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public ListarEstabelecimentosUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public List<Estabelecimento> executar() {
        return estabelecimentoPort.buscarTodos();
    }
}
