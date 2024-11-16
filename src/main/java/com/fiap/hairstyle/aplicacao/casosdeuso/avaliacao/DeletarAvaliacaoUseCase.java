package com.fiap.hairstyle.aplicacao.casosdeuso.avaliacao;

import com.fiap.hairstyle.dominio.portas.saida.AvaliacaoPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarAvaliacaoUseCase {

    private final AvaliacaoPort avaliacaoPort;

    public DeletarAvaliacaoUseCase(AvaliacaoPort avaliacaoPort) {
        this.avaliacaoPort = avaliacaoPort;
    }

    public void executar(UUID avaliacaoId) {
        if (!avaliacaoPort.existePorId(avaliacaoId)) {
            throw new IllegalArgumentException("Avaliação não encontrada.");
        }
        avaliacaoPort.deletarPorId(avaliacaoId);
    }
}
