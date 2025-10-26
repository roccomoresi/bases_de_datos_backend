package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data

@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;


    @Column(nullable = false, unique = true, length = 50)
    private String descripcion; // Ej: "ADMIN", "TECNICO", "USUARIO"


/*
     * 📌 Lombok @Data genera automáticamente:
     * ----------------------------------------
     * public Integer getIdRol() {
     *     return idRol;
     * }
     *
     * public void setIdRol(Integer idRol) {
     *     this.idRol = idRol;
     * }
     *
     * public String getDescripcion() {
     *     return descripcion;
     * }
     *
     * public void setDescripcion(String descripcion) {
     *     this.descripcion = descripcion;
     * }
     *
     * Además, incluye:
     *  - toString()
     *  - equals() y hashCode()
     *  - constructor por defecto
     */


}
