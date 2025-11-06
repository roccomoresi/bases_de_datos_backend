package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.SesionResponse;
import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.service.sql.SesionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Sesiones", description = "Gestión de sesiones de usuarios (solo visible para ADMIN)")
@RestController
@RequestMapping("/api/sql/sesiones")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @GetMapping("/activas")
    public ResponseEntity<?> listarSesionesActivas() {
        List<Sesion> activas = sesionService.obtenerSesionesActivas();

        List<SesionResponse> resp = activas.stream()
        .map(s -> new SesionResponse(
                s.getIdSesion(),
                s.getUsuario().getIdUsuario(),
                s.getUsuario().getNombreCompleto(),
                s.getUsuario().getEmail(),
                s.getRol(),
                s.getFechaInicio(),
                s.getEstado().name()
        ))
        .toList();



        return ResponseEntity.ok(resp);
    }

    @GetMapping("/todas")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<SesionResponse>> listarTodas() {
    var sesiones = sesionService.obtenerTodasLasSesiones();
    var resp = sesiones.stream()
            .map(s -> new SesionResponse(
                    s.getIdSesion(),
                    s.getUsuario().getIdUsuario(),
                    s.getUsuario().getNombreCompleto(),
                    s.getUsuario().getEmail(),
                    s.getRol(),
                    s.getFechaInicio(),
                    s.getEstado().name() // ACTIVA o INACTIVA
            ))
            .toList();
    return ResponseEntity.ok(resp);
}

}
