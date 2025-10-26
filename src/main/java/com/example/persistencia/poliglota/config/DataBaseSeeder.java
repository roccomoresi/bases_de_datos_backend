package com.example.persistencia.poliglota.config;
//Crea los roles al iniciar si no existen


import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.repository.sql.RolRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class DataBaseSeeder {

    private final RolRepository rolRepository;

    public DataBaseSeeder(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @PostConstruct
    public void seedRoles() {
        List<String> rolesBase = List.of("ADMIN", "TECNICO", "USUARIO");

        for (String desc : rolesBase) {
            if (rolRepository.findByDescripcion(desc).isEmpty()) {
                Rol rol = new Rol();
                rol.setDescripcion(desc);
                rolRepository.save(rol);
                log.info("✅ Rol creado automáticamente: {}", desc);
            }
        }
    }
}

