package com.example.persistencia.poliglota.controller.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Usuario;
import com.example.persistencia.poliglota.service.neo4j.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neo4j/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }



    @GetMapping
public List<String> getAllUsuarios() {
    return usuarioService.getAllUsuarios();
}


}
