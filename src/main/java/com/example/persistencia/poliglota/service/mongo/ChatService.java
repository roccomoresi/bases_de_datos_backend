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

    // Crear un nuevo chat entre usuarios
    public Chat crearChat(List<String> participantes) {
        Chat chat = new Chat();
        chat.setParticipantes(participantes);
        chat.setUltimaActualizacion(Instant.now());
        return repository.save(chat);
    }

    // Enviar un mensaje en un chat existente
    public Chat enviarMensaje(String chatId, String remitente, String contenido) {
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("No se encontró el chat con ID " + chatId));

        Chat.Mensaje mensaje = new Chat.Mensaje(remitente, contenido, Instant.now());
        chat.getMensajes().add(mensaje);
        chat.setUltimaActualizacion(Instant.now());

        return repository.save(chat);
    }

    // Obtener un chat por ID
    public Chat obtenerChat(String chatId) {
        return repository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("No se encontró el chat con ID " + chatId));
    }

    // Listar todos los chats de un usuario
    public List<Chat> listarChatsPorUsuario(String usuarioId) {
        return repository.findByParticipantesContaining(usuarioId);
    }
}
