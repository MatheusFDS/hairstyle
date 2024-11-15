package com.fiap.hairstyle.adaptadores.saida.repositorios;

import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, UUID> {

    @Query("SELECT h FROM HorarioDisponivel h WHERE h.profissional.id = :profissionalId AND h.diaSemana = :diaSemana")
    List<HorarioDisponivel> findByProfissionalIdAndDiaSemana(@Param("profissionalId") UUID profissionalId, @Param("diaSemana") DayOfWeek diaSemana);

    List<HorarioDisponivel> findByProfissionalId(UUID profissionalId); // Listar por profissional
}
