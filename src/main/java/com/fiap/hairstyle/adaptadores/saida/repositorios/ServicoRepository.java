package com.fiap.hairstyle.dominio.repositorios;

import com.fiap.hairstyle.dominio.entidades.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, UUID> {
    // Métodos personalizados podem ser adicionados aqui, se necessário
}
