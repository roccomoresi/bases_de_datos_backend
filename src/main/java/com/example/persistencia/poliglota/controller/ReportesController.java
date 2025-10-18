package com.example.persistencia.poliglota.controller;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import com.example.persistencia.poliglota.service.cassandra.SensorService;
import com.example.persistencia.poliglota.service.mongo.AlertaService;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin
public class ReportesController {

    private final UsuarioService usuarioService;
    private final FacturaService facturaService;
    private final SensorService sensorService;
    private final AlertaService alertaService;

    public ReportesController(UsuarioService usuarioService,
                              FacturaService facturaService,
                              SensorService sensorService,
                              AlertaService alertaService) {
        this.usuarioService = usuarioService;
        this.facturaService = facturaService;
        this.sensorService = sensorService;
        this.alertaService = alertaService;
    }

    @GetMapping("/usuario/{id}")
    public Map<String, Object> getReporteGlobal(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();

        // 🧠 Datos relacionales (MySQL)
        Usuario usuario = usuarioService.getById(id).orElseThrow();
        List<Factura> facturas = facturaService.getFacturasPorUsuario(usuario);

        // ⚙️ Datos tabulares (Cassandra)
        List<Sensor> sensores = sensorService.getAll(); // simplificado: todos los sensores
        List<Sensor> sensoresUsuario = sensores.stream()
                .filter(s -> s.getCiudad().equalsIgnoreCase("Buenos Aires"))
                .toList(); // ejemplo de criterio

        // 🌍 Datos de grafos (Neo4j)
        List<Alerta> alertas = alertaService.getAll();

        response.put("usuario", usuario);
        response.put("facturas", facturas);
        response.put("sensores", sensoresUsuario);
        response.put("alertas", alertas);

        return response;
    }
}
