package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Chat;
import com.example.persistencia.poliglota.repository.mongo.ChatRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository repository;

    public ChatService(ChatRepository repository) {
        this.repository = repository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Crear un chat privado (2 participantes)
    // ─────────────────────────────────────────────────────────────────────────
    public Chat crearChat(List<String> participantes) {
        Chat chat = new Chat();
        chat.setParticipantes(participantes);
        chat.setTipo("privado");
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Crear un chat de grupo
    // ─────────────────────────────────────────────────────────────────────────
    public Chat crearGrupo(String nombre, List<String> participantes) {
        Chat chat = new Chat();
        chat.setNombreGrupo(nombre);
        chat.setParticipantes(participantes);
        chat.setTipo("grupo");
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Enviar mensaje
    // ─────────────────────────────────────────────────────────────────────────
    public Chat enviarMensaje(String chatId, String remitente, String contenido) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        Chat.Mensaje mensaje = new Chat.Mensaje(remitente, contenido, Instant.now(), false);
        chat.getMensajes().add(mensaje);
        chat.setUltimaActualizacion(Instant.now());

        return repository.save(chat);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Obtener un chat por ID
    // ─────────────────────────────────────────────────────────────────────────
    public Chat obtenerChat(String chatId) {
        return repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("No se encontró el chat con ID " + chatId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listar chats donde participa un usuario
    // ─────────────────────────────────────────────────────────────────────────
    public List<Chat> listarChatsPorUsuario(String usuarioId) {
        return repository.findByParticipantesContaining(usuarioId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listar todos los chats por fecha de última actualización (desc)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Chat> listarRecientes() {
        return repository.findAllByOrderByUltimaActualizacionDesc();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Marcar mensaje como LEÍDO
    // ─────────────────────────────────────────────────────────────────────────
    public Chat marcarMensajeComoLeido(String chatId, int indexMensaje) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (indexMensaje >= 0 && indexMensaje < chat.getMensajes().size()) {
            chat.getMensajes().get(indexMensaje).setLeido(true);
            chat.setUltimaActualizacion(Instant.now());
            return repository.save(chat);
        } else {
            throw new RuntimeException("Índice de mensaje inválido");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Marcar mensaje como NO LEÍDO
    // ─────────────────────────────────────────────────────────────────────────
    public Chat marcarMensajeComoNoLeido(String chatId, int indexMensaje) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (indexMensaje >= 0 && indexMensaje < chat.getMensajes().size()) {
            chat.getMensajes().get(indexMensaje).setLeido(false);
            chat.setUltimaActualizacion(Instant.now());
            return repository.save(chat);
        } else {
            throw new RuntimeException("Índice de mensaje inválido");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Resumen de chats recientes (para listas: último mensaje, remitente, etc.)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> listarRecientesResumen() {
        List<Chat> chats = repository.findAllByOrderByUltimaActualizacionDesc();

        return chats.stream().map(chat -> {
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("idchat", chat.getId());

            // tipo (evita NPE)
            String tipo = chat.getTipo() != null ? chat.getTipo() : "desconocido";
            resumen.put("tipo", tipo);

            // nombre (si es grupo)
            if ("grupo".equals(tipo)) {
                resumen.put("nombre", chat.getNombreGrupo());
            }

            // último mensaje
            if (chat.getMensajes() != null && !chat.getMensajes().isEmpty()) {
                Chat.Mensaje ultimo = chat.getMensajes().get(chat.getMensajes().size() - 1);
                resumen.put("ultimoMensaje", ultimo.getContenido());
                resumen.put("remitente", ultimo.getRemitente());
                resumen.put("leido", ultimo.isLeido());
            } else {
                resumen.put("ultimoMensaje", "(sin mensajes)");
                resumen.put("remitente", null);
                resumen.put("leido", true);
            }

            resumen.put("ultimaActualizacion", chat.getUltimaActualizacion());

            // opcional: concatenar participantes para mostrar
            if (chat.getParticipantes() != null) {
                resumen.put("participantes", String.join(", ", chat.getParticipantes()));
            }

            return resumen;
        }).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Eliminar un chat completo
    // ─────────────────────────────────────────────────────────────────────────
    public void eliminarChat(String chatId) {
        if (!repository.existsById(chatId)) {
            throw new RuntimeException("No se encontró el chat con ID " + chatId);
        }
        repository.deleteById(chatId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Eliminar un mensaje por índice dentro del chat
    // ─────────────────────────────────────────────────────────────────────────
    public Chat eliminarMensaje(String chatId, int indexMensaje) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (indexMensaje >= 0 && indexMensaje < chat.getMensajes().size()) {
            chat.getMensajes().remove(indexMensaje);
            chat.setUltimaActualizacion(Instant.now());
            return repository.save(chat);
        } else {
            throw new RuntimeException("Índice de mensaje inválido");
        }
    }
}