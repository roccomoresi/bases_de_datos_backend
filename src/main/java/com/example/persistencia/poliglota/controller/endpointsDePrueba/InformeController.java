package com.example.persistencia.poliglota.controller.endpointsDePrueba;

import com.example.persistencia.poliglota.dto.InformeCiudadRequest;
import com.example.persistencia.poliglota.dto.InformeCiudadResponse;
import com.example.persistencia.poliglota.dto.InformePaisResponse;
import com.example.persistencia.poliglota.dto.InformeSeriePunto;
import com.example.persistencia.poliglota.service.cassandra.InformeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/informes")
public class InformeController {

    private final InformeService informeService;

    public InformeController(InformeService informeService) {
        this.informeService = informeService;
    }

    // POST body con ciudad/pais y rangos
    @PostMapping("/ciudad")
    public ResponseEntity<InformeCiudadResponse> informeCiudad(@RequestBody InformeCiudadRequest req) {
        return ResponseEntity.ok(informeService.calcularPorCiudad(req));
    }

    // GET con query params para facilitar pruebas rápidas
    @GetMapping("/ciudad")
    public ResponseEntity<InformeCiudadResponse> informeCiudadGet(
            @RequestParam String ciudad,
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        InformeCiudadRequest req = new InformeCiudadRequest();
        req.setCiudad(ciudad);
        req.setPais(pais);
        req.setDesde(desde);
        req.setHasta(hasta);
        return ResponseEntity.ok(informeService.calcularPorCiudad(req));
    }

    // País: agregados por país, con fechas opcionales
    @GetMapping("/pais")
    public ResponseEntity<InformePaisResponse> informePaisGet(
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        return ResponseEntity.ok(informeService.calcularPorPais(pais, desde, hasta));
    }

    // Series temporalizadas
    @GetMapping("/ciudad/mensual")
    public ResponseEntity<java.util.List<InformeSeriePunto>> serieCiudadMensual(
            @RequestParam String ciudad,
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        return ResponseEntity.ok(informeService.serieCiudadMensual(ciudad, pais, desde, hasta));
    }

    @GetMapping("/ciudad/anual")
    public ResponseEntity<java.util.List<InformeSeriePunto>> serieCiudadAnual(
            @RequestParam String ciudad,
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        return ResponseEntity.ok(informeService.serieCiudadAnual(ciudad, pais, desde, hasta));
    }

    @GetMapping("/pais/mensual")
    public ResponseEntity<java.util.List<InformeSeriePunto>> seriePaisMensual(
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        return ResponseEntity.ok(informeService.seriePaisMensual(pais, desde, hasta));
    }

    @GetMapping("/pais/anual")
    public ResponseEntity<java.util.List<InformeSeriePunto>> seriePaisAnual(
            @RequestParam String pais,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta
    ) {
        return ResponseEntity.ok(informeService.seriePaisAnual(pais, desde, hasta));
    }
}


