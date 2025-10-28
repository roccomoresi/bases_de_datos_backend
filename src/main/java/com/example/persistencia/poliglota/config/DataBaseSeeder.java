package com.example.persistencia.poliglota.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.repository.sql.RolRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataBaseSeeder {

    @Autowired
    private RolRepository rolRepository;

    @PostConstruct
    public void seed() {
        String[] roles = {"usuario", "admin", "tecnico"};
        for (String desc : roles) {
            if (rolRepository.findByDescripcion(desc).isEmpty()) {
                Rol rol = new Rol();
                rol.setDescripcion(desc);
                rolRepository.save(rol);
                log.info("✅ Rol creado automáticamente: {}", desc); // ahora sí reconoce log
            }
        }
    }
}