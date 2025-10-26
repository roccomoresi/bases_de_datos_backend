package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {


List<Sesion> findByUsuario_IdUsuario(Integer idUsuario);


    // opcional: sesiones activas
    List<Sesion> findByEstado(String estado);


}


