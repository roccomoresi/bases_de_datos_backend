package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.repository.sql.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SesionService {
    private final SesionRepository repository;

    public SesionService(SesionRepository repository) {
        this.repository = repository;
    }

    public List<Sesion> obtenerHistorialSesiones(Integer usuarioId) {
    return repository.findByUsuario_IdUsuario(usuarioId);
}

  /* 📅 Registrar inicio de sesión */
    public Sesion registrarInicioSesion(Integer usuarioId) {
        Sesion sesion = new Sesion();
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setEstado("activa");

        // vincular con usuario sin tener que cargarlo completo
        var usuario = new com.example.persistencia.poliglota.model.sql.Usuario();
        usuario.setIdUsuario(usuarioId);
        sesion.setUsuario(usuario);

        return repository.save(sesion);
    }

     /* 🚪 Registrar cierre de sesión */
    public void cerrarSesion(Integer idSesion) {
        Sesion sesion = repository.findById(idSesion)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setEstado("cerrada");
        repository.save(sesion);
    }


    public List<Sesion> getAll() {
        return repository.findAll();
    }

    public Sesion save(Sesion sesion) {
        return repository.save(sesion);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
