package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Mensaje;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.MensajeService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioService usuarioService;

    public MensajeController(MensajeService mensajeService, UsuarioService usuarioService) {
        this.mensajeService = mensajeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuario/{id}")
    public List<Mensaje> getMensajesPorUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.getById(id).orElseThrow();
        return mensajeService.getMensajesDeUsuario(usuario);
    }

    @PostMapping
    public Mensaje enviarMensaje(@RequestBody Mensaje mensaje) {
        return mensajeService.enviarMensaje(mensaje);
    }
}
