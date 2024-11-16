package com.fiap.hairstyle.aplicacao.casosdeuso.servico;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CriarServicoUseCase {

    private final ServicoPort servicoPort;
    private final EstabelecimentoPort estabelecimentoPort;

    public CriarServicoUseCase(ServicoPort servicoPort, EstabelecimentoPort estabelecimentoPort) {
        this.servicoPort = servicoPort;
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public Servico executar(Servico servico) {
        Optional<Estabelecimento> estabelecimento = estabelecimentoPort.buscarPorId(servico.getEstabelecimento().getId());
        if (estabelecimento.isEmpty()) {
            throw new IllegalArgumentException("Estabelecimento não encontrado.");
        }
        servico.setEstabelecimento(estabelecimento.get());
        return servicoPort.salvar(servico);
    }
}
