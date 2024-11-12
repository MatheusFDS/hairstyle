package com.fiap.hairstyle.dominio.repositorios;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {

    @Query("SELECT a FROM Agendamento a WHERE a.profissional.id = :profissionalId AND a.dataHora = :dataHora")
    List<Agendamento> findByProfissionalAndDataHora(@Param("profissionalId") UUID profissionalId, @Param("dataHora") LocalDateTime dataHora);

    @Query("SELECT a FROM Agendamento a WHERE a.cliente.id = :clienteId AND a.dataHora = :dataHora")
    List<Agendamento> findByClienteAndDataHora(@Param("clienteId") UUID clienteId, @Param("dataHora") LocalDateTime dataHora);

    @Query("SELECT a FROM Agendamento a WHERE a.cliente.id = :clienteId")
    List<Agendamento> findByClienteId(@Param("clienteId") UUID clienteId);

    @Query("SELECT a FROM Agendamento a WHERE a.profissional.id = :profissionalId")
    List<Agendamento> findByProfissionalId(@Param("profissionalId") UUID profissionalId);

    @Query("SELECT a FROM Agendamento a WHERE a.dataHora BETWEEN :inicio AND :fim")
    List<Agendamento> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
