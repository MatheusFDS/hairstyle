package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarEstabelecimentoPorIdUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public BuscarEstabelecimentoPorIdUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public Optional<Estabelecimento> executar(UUID id) {
        return estabelecimentoPort.buscarPorId(id);
    }
}
