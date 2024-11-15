package com.fiap.hairstyle.adaptadores.saida.repositorios;

import com.fiap.hairstyle.dominio.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
