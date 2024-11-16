package com.fiap.hairstyle.adaptadores.saida.adapter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.ProfissionalRepository;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfissionalAdapter implements ProfissionalPort {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalAdapter(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    @Override
    public List<Profissional> buscarTodos() {
        return profissionalRepository.findAll();
    }

    @Override
    public Optional<Profissional> buscarPorId(UUID id) {
        return profissionalRepository.findById(id);
    }

    @Override
    public Profissional salvar(Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    @Override
    public void deletar(UUID id) {
        profissionalRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(UUID id) {
        return profissionalRepository.existsById(id);
    }
}
