package com.example.persistencia.poliglota.service.sql;


import com.example.persistencia.poliglota.model.sql.EstadoSesion;
import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.SesionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SesionService {

    private final SesionRepository sesionRepository;
    private final UsuarioService usuarioService;

    public SesionService(SesionRepository sesionRepository, UsuarioService usuarioService) {
        this.sesionRepository = sesionRepository;
        this.usuarioService = usuarioService;
    }

    // 🔹 Se usa al hacer login
    public Sesion registrarInicioSesion(Integer idUsuario, String rol) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        sesion.setRol(rol);
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setEstado(EstadoSesion.ACTIVA);

        Sesion guardada = sesionRepository.save(sesion);
        log.info("✅ Sesión iniciada para usuario {} (idSesion={})", usuario.getEmail(), guardada.getIdSesion());
        return guardada;
    }

    // 🔹 Se usa al hacer logout
    @Transactional
public void cerrarSesion(Long idSesion) {  // 👈 también Long
    Sesion sesion = sesionRepository.findById(idSesion)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
    sesion.setEstado(EstadoSesion.INACTIVA);
    sesion.setFechaCierre(LocalDateTime.now());
    sesionRepository.save(sesion);
}

public List<Sesion> obtenerTodasLasSesiones() {
    return sesionRepository.findAll();
}



    // 🔹 Se usa por el ADMIN para ver sesiones activas
    public List<Sesion> obtenerSesionesActivas() {
        List<Sesion> activas = sesionRepository.findByEstado(EstadoSesion.ACTIVA);
        log.info("📊 Sesiones activas encontradas: {}", activas.size());
        return activas;
    }


}
