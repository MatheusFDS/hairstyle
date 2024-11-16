package com.fiap.hairstyle.aplicacao.casosdeuso.profissional;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.portas.saida.ProfissionalPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarProfissionalUseCase {

    private final ProfissionalPort profissionalPort;

    public AtualizarProfissionalUseCase(ProfissionalPort profissionalPort) {
        this.profissionalPort = profissionalPort;
    }

    public Optional<Profissional> executar(UUID id, Profissional profissionalAtualizado) {
        return profissionalPort.buscarPorId(id).map(profissional -> {
            profissional.setNome(profissionalAtualizado.getNome());
            profissional.setEspecialidade(profissionalAtualizado.getEspecialidade());
            profissional.setTelefone(profissionalAtualizado.getTelefone());
            profissional.setTarifa(profissionalAtualizado.getTarifa());
            return profissionalPort.salvar(profissional);
        });
    }
}
