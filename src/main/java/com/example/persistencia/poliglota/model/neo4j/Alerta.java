package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;
import java.time.LocalDateTime;

@Node("Alerta")
public class Alerta {

    @Id @GeneratedValue
    private Long id;

    private String tipo; // sensor o climática
    private String descripcion;
    private String estado; // activa / resuelta
    private LocalDateTime fechaHora;

    @Relationship(type = "GENERADA_POR", direction = Relationship.Direction.OUTGOING)
    private SensorNode sensor;

    // Getters y Setters
}
