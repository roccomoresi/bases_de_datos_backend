package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Chat;
import com.example.persistencia.poliglota.repository.mongo.ChatRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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

    // 🔹 Listar conversaciones recientes
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

}
