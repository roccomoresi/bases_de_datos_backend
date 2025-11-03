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

    // 🔹 Crear un chat privado
    public Chat crearChat(List<String> participantes) {
        Chat chat = new Chat();
        chat.setParticipantes(participantes);
        chat.setTipo("privado");
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // 🔹 Crear un grupo
    public Chat crearGrupo(String nombre, List<String> participantes) {
        Chat chat = new Chat();
        chat.setNombreGrupo(nombre);
        chat.setParticipantes(participantes);
        chat.setTipo("grupo");
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // 🔹 Enviar mensaje
    public Chat enviarMensaje(String chatId, String remitente, String contenido) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("No se encontró el chat con ID " + chatId));

        Chat.Mensaje mensaje = new Chat.Mensaje(remitente, contenido, Instant.now(), false);
        chat.getMensajes().add(mensaje);
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // 🔹 Obtener un chat
    public Chat obtenerChat(String chatId) {
        return repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("No se encontró el chat con ID " + chatId));
    }

    // 🔹 Listar chats de un usuario
    public List<Chat> listarChatsPorUsuario(String usuarioId) {
        return repository.findByParticipantesContaining(usuarioId);
    }

    // 🔹 Listar todos los chats por fecha
    public List<Chat> listarRecientes() {
        return repository.findAllByOrderByUltimaActualizacionDesc();
    }

    // 🔹 Marcar mensaje como leído
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

    // 🔹 Marcar mensaje como NO leído
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

    // 🔹 Listar resumen de chats recientes (último mensaje)
    public List<Map<String, Object>> listarRecientesResumido() {
        List<Chat> chats = repository.findAllByOrderByUltimaActualizacionDesc();
        return chats.stream().map(chat -> {
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("idChat", chat.getId());
            resumen.put("tipo", chat.getTipo());
            resumen.put("nombre", chat.getTipo().equals("grupo")
                    ? chat.getNombreGrupo()
                    : String.join(", ", chat.getParticipantes()));
            resumen.put("ultimaActualizacion", chat.getUltimaActualizacion());

            if (!chat.getMensajes().isEmpty()) {
                Chat.Mensaje ultimo = chat.getMensajes().get(chat.getMensajes().size() - 1);
                resumen.put("ultimoMensaje", ultimo.getContenido());
                resumen.put("remitente", ultimo.getRemitente());
                resumen.put("leido", ultimo.isLeido());
            } else {
                resumen.put("ultimoMensaje", "(sin mensajes)");
                resumen.put("remitente", null);
                resumen.put("leido", true);
            }

            return resumen;
        }).collect(Collectors.toList());
    }
    // 🗑️ Eliminar un chat por ID
    public void eliminarChat(String chatId) {
        if (!repository.existsById(chatId)) {
            throw new RuntimeException("No se encontró el chat con ID " + chatId);
        }
        repository.deleteById(chatId);
    }

    // 🗑️ Eliminar un mensaje por índice dentro del chat
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
