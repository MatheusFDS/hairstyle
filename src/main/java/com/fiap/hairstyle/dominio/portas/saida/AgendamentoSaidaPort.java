package com.fiap.hairstyle.dominio.portas.saida;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import java.time.LocalDateTime;
import java.util.UUID;

public interface AgendamentoSaidaPort {
    Agendamento salvar(Agendamento agendamento);
    boolean existeAgendamentoNoHorario(UUID profissionalId, LocalDateTime dataHora);
}
