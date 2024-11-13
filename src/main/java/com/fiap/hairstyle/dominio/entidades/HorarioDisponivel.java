package com.fiap.hairstyle.dominio.entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class HorarioDisponivel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana; // Alterado para "diaSemana" para consistência

    private LocalTime horaInicio;
    private LocalTime horaFim;

}
