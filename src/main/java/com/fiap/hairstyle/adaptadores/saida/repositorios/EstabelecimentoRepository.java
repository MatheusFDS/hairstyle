package com.fiap.hairstyle.adaptadores.saida.repositorios;

import com.fiap.hairstyle.adaptadores.saida.repositorios.custom.EstabelecimentoRepositoryCustom;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, UUID>, EstabelecimentoRepositoryCustom {
}
