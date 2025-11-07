package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.service.mongo.ProcesoExecutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/procesos")
public class ProcesoExecutorController {

    private final ProcesoExecutorService executorService;

    public ProcesoExecutorController(ProcesoExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Ejecuta manualmente un proceso técnico (sin crear factura nueva).
     * Ejemplo:
     * POST /api/procesos/ejecutar?usuarioId=1&procesoId=PROC-001
     */
    @PostMapping("/ejecutar")
    public ResponseEntity<Map<String, Object>> ejecutar(
            @RequestParam Integer usuarioId,
            @RequestParam String procesoId
    ) {
        try {
            String resultado = executorService.ejecutarProceso(usuarioId, procesoId);

            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("procesoId", procesoId);
            body.put("resultado", resultado);
            body.put("timestamp", LocalDateTime.now());
            body.put("status", "OK");

            return ResponseEntity.ok(body);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("Parámetros inválidos", e.getMessage()));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("no encontrado")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.INTERNAL_SERVER_ERROR;

            return ResponseEntity.status(status)
                    .body(error("Error al ejecutar proceso", e.getMessage()));
        }
    }

    /** Maneja errores por parámetros faltantes (?usuarioId / ?procesoId) */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("Falta parámetro requerido", ex.getParameterName()));
    }

    // Helper para formato de error
    private Map<String, Object> error(String titulo, String detalle) {
        Map<String, Object> err = new HashMap<>();
        err.put("error", titulo);
        err.put("detalle", detalle);
        err.put("timestamp", LocalDateTime.now());
        return err;
    }
}
