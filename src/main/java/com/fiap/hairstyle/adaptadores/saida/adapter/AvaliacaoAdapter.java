package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.AvaliacaoRepository;
import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import com.fiap.hairstyle.dominio.portas.saida.AvaliacaoPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AvaliacaoAdapter implements AvaliacaoPort {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoAdapter(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public Optional<Avaliacao> buscarPorId(UUID id) {
        return avaliacaoRepository.findById(id);
    }

    @Override
    public List<Avaliacao> buscarPorProfissional(UUID profissionalId) {
        return avaliacaoRepository.findByProfissionalId(profissionalId);
    }

    @Override
    public List<Avaliacao> buscarPorEstabelecimento(UUID estabelecimentoId) {
        return avaliacaoRepository.findByEstabelecimentoId(estabelecimentoId);
    }

    @Override
    public Double calcularMediaPorProfissional(UUID profissionalId) {
        return avaliacaoRepository.calcularMediaPorProfissional(profissionalId);
    }

    @Override
    public Double calcularMediaPorEstabelecimento(UUID estabelecimentoId) {
        return avaliacaoRepository.calcularMediaPorEstabelecimento(estabelecimentoId);
    }

    @Override
    public boolean existePorId(UUID id) {
        return avaliacaoRepository.existsById(id);
    }

    @Override
    public void deletarPorId(UUID id) {
        avaliacaoRepository.deleteById(id);
    }
}
