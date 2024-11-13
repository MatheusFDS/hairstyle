package com.fiap.hairstyle.dominio.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(1)
    @Max(5)
    private int nota; // Nota de 1 a 5, com validação

    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profissional_id", nullable = true)
    @JsonBackReference
    private Profissional profissional;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estabelecimento_id", nullable = true)
    @JsonBackReference
    private Estabelecimento estabelecimento;
}
