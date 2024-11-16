package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.ServicoRepository;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServicoAdapter implements ServicoPort {

    private final ServicoRepository servicoRepository;

    public ServicoAdapter(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return servicoRepository.findById(id);
    }

    @Override
    public List<Servico> buscarTodos() {
        return servicoRepository.findAll();
    }

    @Override
    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    @Override
    public void deletar(UUID id) {
        servicoRepository.deleteById(id); // Implementado o método deletar
    }

    @Override
    public boolean existePorId(UUID id) {
        return servicoRepository.existsById(id);
    }
}
