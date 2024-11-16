package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Agendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoPort {
    Optional<Agendamento> buscarPorId(UUID id);
    List<Agendamento> buscarTodos();
    Optional<Agendamento> buscarPorProfissionalEHorario(UUID profissionalId, LocalDateTime dataHora);
    Optional<Agendamento> buscarPorClienteEHorario(UUID clienteId, LocalDateTime dataHora);
    Agendamento salvar(Agendamento agendamento);
    void deletar(Agendamento agendamento);
}
