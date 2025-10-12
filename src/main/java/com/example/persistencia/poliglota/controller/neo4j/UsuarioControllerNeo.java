package com.example.persistencia.poliglota.controller.neo4j;

import com.example.persistencia.poliglota.model.neo4j.UsuarioNeo;
import com.example.persistencia.poliglota.service.neo4j.UsuarioServiceNeo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neo4j/usuarios")
public class UsuarioControllerNeo {

    private final UsuarioServiceNeo usuarioService;

    public UsuarioControllerNeo(UsuarioServiceNeo usuarioService) {
        this.usuarioService = usuarioService;
    }



    @GetMapping
public List<String> getAllUsuarios() {
    return usuarioService.getAllUsuarios();
}


}
