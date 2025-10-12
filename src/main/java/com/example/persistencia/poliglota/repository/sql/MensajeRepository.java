package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Mensaje;
import com.example.persistencia.poliglota.model.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteOrDestinatario(Usuario remitente, Usuario destinatario);
}
