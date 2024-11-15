package com.fiap.hairstyle.dominio.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue
    private UUID id;

    private int nota;
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference // Define o lado "filho" para cliente
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = true)
    @JsonBackReference // Define o lado "filho" para profissional
    private Profissional profissional;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = true)
    @JsonBackReference // Define o lado "filho" para estabelecimento
    private Estabelecimento estabelecimento;
}
