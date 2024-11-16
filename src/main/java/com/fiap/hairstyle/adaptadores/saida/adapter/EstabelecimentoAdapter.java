package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.EstabelecimentoRepository;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.portas.saida.EstabelecimentoPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EstabelecimentoAdapter implements EstabelecimentoPort {

    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoAdapter(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Override
    public Estabelecimento salvar(Estabelecimento estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento);
    }

    @Override
    public Optional<Estabelecimento> buscarPorId(UUID id) {
        return estabelecimentoRepository.findById(id);
    }

    @Override
    public List<Estabelecimento> buscarTodos() {
        return estabelecimentoRepository.findAll();
    }

    @Override
    public void deletarPorId(UUID id) {
        estabelecimentoRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(UUID id) {
        return estabelecimentoRepository.existsById(id);
    }

    @Override
    public List<Estabelecimento> filtrar(String nome, String endereco, Double precoMin, Double precoMax, String servico, Double avaliacaoMinima) {
        return estabelecimentoRepository.filtrarEstabelecimentos(nome, endereco, precoMin, precoMax, servico, avaliacaoMinima);
    }
}
