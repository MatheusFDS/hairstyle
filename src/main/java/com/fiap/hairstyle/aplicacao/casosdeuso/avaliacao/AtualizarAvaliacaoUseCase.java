package com.fiap.hairstyle.aplicacao.casosdeuso.avaliacao;

import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import com.fiap.hairstyle.dominio.portas.saida.AvaliacaoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarAvaliacaoUseCase {

    private final AvaliacaoPort avaliacaoPort;

    public AtualizarAvaliacaoUseCase(AvaliacaoPort avaliacaoPort) {
        this.avaliacaoPort = avaliacaoPort;
    }

    public Avaliacao executar(UUID avaliacaoId, Avaliacao avaliacaoAtualizada) {
        Avaliacao avaliacao = avaliacaoPort.buscarPorId(avaliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        avaliacao.setNota(avaliacaoAtualizada.getNota());
        avaliacao.setComentario(avaliacaoAtualizada.getComentario());

        return avaliacaoPort.salvar(avaliacao);
    }
}
