package com.example.persistencia.poliglota.controller.endpointsDePrueba;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String soloAdmin() {
        return "Bienvenido Admin ✅";
    }

    @GetMapping("/tecnico")
    @PreAuthorize("hasAuthority('ROLE_TECNICO')")
    public String soloTecnico() {
        return "Bienvenido Técnico 🔧";
    }

    @GetMapping("/usuario")
    @PreAuthorize("hasAuthority('ROLE_USUARIO')")
    public String soloUsuario() {
        return "Bienvenido Usuario 🙌";
    }
}

