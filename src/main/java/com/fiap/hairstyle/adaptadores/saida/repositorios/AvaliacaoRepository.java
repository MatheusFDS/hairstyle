package com.fiap.hairstyle.dominio.repositorios;

import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    // Listar avaliações de um profissional específico
    List<Avaliacao> findByProfissionalId(UUID profissionalId);

    // Listar avaliações de um estabelecimento específico
    List<Avaliacao> findByEstabelecimentoId(UUID estabelecimentoId);

    // Calcular média de notas para um profissional específico
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.profissional.id = :profissionalId")
    Double calcularMediaPorProfissional(@Param("profissionalId") UUID profissionalId);

    // Calcular média de notas para um estabelecimento específico
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.estabelecimento.id = :estabelecimentoId")
    Double calcularMediaPorEstabelecimento(@Param("estabelecimentoId") UUID estabelecimentoId);
}
