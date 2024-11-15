package com.fiap.hairstyle.adaptadores.saida.repositorios;

import com.fiap.hairstyle.dominio.entidades.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {
    // Métodos personalizados para Profissional podem ser adicionados aqui
}
