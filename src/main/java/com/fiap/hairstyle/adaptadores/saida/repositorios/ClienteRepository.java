package com.fiap.hairstyle.dominio.repositorios;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    // Métodos personalizados podem ser adicionados aqui
}
