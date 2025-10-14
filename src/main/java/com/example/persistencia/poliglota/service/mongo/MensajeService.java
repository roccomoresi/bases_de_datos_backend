package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Mensaje;
import com.example.persistencia.poliglota.repository.mongo.MensajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MensajeService {

    private final MensajeRepository repository;

    public MensajeService(MensajeRepository repository) {
        this.repository = repository;
    }

    // 🔹 Obtener todos los mensajes
    public List<Mensaje> getAll() {
        return repository.findAll();
    }

    // 🔹 Obtener un mensaje por ID
    public Mensaje getById(UUID id) {
        Optional<Mensaje> mensaje = repository.findById(id);
        return mensaje.orElse(null);
    }

    // 🔹 Crear o guardar un mensaje nuevo
    public Mensaje save(Mensaje mensaje) {
        return repository.save(mensaje);
    }

    // 🔹 Actualizar un mensaje existente
    public Mensaje update(UUID id, Mensaje mensajeNuevo) {
        return repository.findById(id)
                .map(mensaje -> {
                    mensaje.setContenido(mensajeNuevo.getContenido());
                    mensaje.setTipo(mensajeNuevo.getTipo());
                    mensaje.setFecha(mensajeNuevo.getFecha());
                    mensaje.setRemitenteId(mensajeNuevo.getRemitenteId());
                    mensaje.setDestinatarioId(mensajeNuevo.getDestinatarioId());
                    return repository.save(mensaje);
                })
                .orElse(null);
    }

    // 🔹 Eliminar mensaje
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // 🔹 Buscar mensajes por remitente (si querés filtrar)
    public List<Mensaje> findByRemitenteId(UUID remitenteId) {
        return repository.findByRemitenteId(remitenteId);
    }

    // 🔹 Buscar mensajes por destinatario
    public List<Mensaje> findByDestinatarioId(UUID destinatarioId) {
        return repository.findByDestinatarioId(destinatarioId);
    }
}
