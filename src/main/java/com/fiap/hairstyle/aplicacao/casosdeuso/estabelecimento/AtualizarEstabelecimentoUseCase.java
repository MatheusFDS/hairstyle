package com.fiap.hairstyle.aplicacao.casosdeuso.estabelecimento;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarEstabelecimentoUseCase {

    private final EstabelecimentoPort estabelecimentoPort;

    public AtualizarEstabelecimentoUseCase(EstabelecimentoPort estabelecimentoPort) {
        this.estabelecimentoPort = estabelecimentoPort;
    }

    public Optional<Estabelecimento> executar(UUID id, Estabelecimento estabelecimentoAtualizado) {
        return estabelecimentoPort.buscarPorId(id).map(estabelecimento -> {
            estabelecimento.setNome(estabelecimentoAtualizado.getNome());
            estabelecimento.setEndereco(estabelecimentoAtualizado.getEndereco());
            estabelecimento.setServicos(estabelecimentoAtualizado.getServicos());
            estabelecimento.setProfissionais(estabelecimentoAtualizado.getProfissionais());
            estabelecimento.setHorariosFuncionamento(estabelecimentoAtualizado.getHorariosFuncionamento());
            estabelecimento.setFotos(estabelecimentoAtualizado.getFotos());
            return estabelecimentoPort.salvar(estabelecimento);
        });
    }
}
