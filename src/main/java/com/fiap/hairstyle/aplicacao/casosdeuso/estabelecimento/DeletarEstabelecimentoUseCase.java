package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarEstabelecimentoUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public DeletarEstabelecimentoUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public boolean executar(UUID id) {
        if (estabelecimentoPort.existePorId(id)) {
            estabelecimentoPort.deletarPorId(id);
            return true;
        }
        return false;
    }
}
