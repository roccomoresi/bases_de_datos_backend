package com.example.persistencia.poliglota.service.sql;


import com.example.persistencia.poliglota.model.sql.Mensaje;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.MensajeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;

    public MensajeService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public List<Mensaje> getMensajesDeUsuario(Usuario usuario) {
        return mensajeRepository.findByRemitenteOrDestinatario(usuario, usuario);
    }

    public Mensaje enviarMensaje(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }
}
