package com.fiap.hairstyle.aplicacao.casosdeuso.servico;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarServicoUseCase {

    private final ServicoPort servicoPort;
    private final EstabelecimentoPort estabelecimentoPort;

    public AtualizarServicoUseCase(ServicoPort servicoPort, EstabelecimentoPort estabelecimentoPort) {
        this.servicoPort = servicoPort;
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public Servico executar(UUID id, Servico servicoAtualizado) {
        Servico servico = servicoPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        if (servicoAtualizado.getEstabelecimento() != null) {
            Estabelecimento estabelecimento = estabelecimentoPort.buscarPorId(servicoAtualizado.getEstabelecimento().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado."));
            servico.setEstabelecimento(estabelecimento);
        }

        servico.setNome(servicoAtualizado.getNome());
        servico.setDescricao(servicoAtualizado.getDescricao());
        servico.setPreco(servicoAtualizado.getPreco());
        servico.setDuracao(servicoAtualizado.getDuracao());

        return servicoPort.salvar(servico);
    }
}
