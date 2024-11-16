package com.fiap.hairstyle.adaptadores.saida.adpter;

import com.fiap.hairstyle.adaptadores.saida.repositorios.ServicoRepository;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.ServicoPort;
import org.springframework.stereotype.Component;

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
}
