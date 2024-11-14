package com.fiap.hairstyle.dominio.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa um agendamento de um serviço em um estabelecimento.
 * Contém informações sobre o cliente, profissional, serviço, data e hora, e
 * estado do comparecimento, além de integração com Google Calendar.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    /**
     * Identificador único do agendamento.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    /**
     * Cliente associado ao agendamento.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    /**
     * Serviço selecionado para o agendamento.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servico_id", nullable = false)
    @JsonBackReference
    private Servico servico;

    /**
     * Profissional responsável pelo serviço.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profissional_id", nullable = false)
    @JsonBackReference
    private Profissional profissional;

    /**
     * Data e hora do agendamento.
     */
    private LocalDateTime dataHora;

    /**
     * Indica se houve um não comparecimento do cliente ao agendamento.
     */
    private boolean naoComparecimento = false;

    /**
     * Identificador do evento associado no Google Calendar.
     * Utilizado para sincronização com o calendário do cliente/profissional.
     */
    private String googleCalendarEventId;
}
