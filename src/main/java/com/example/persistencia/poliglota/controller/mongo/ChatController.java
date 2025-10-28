package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Chat;
import com.example.persistencia.poliglota.service.mongo.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mongo/chats")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    // 🗨️ Crear chat
    @PostMapping
    public ResponseEntity<Chat> crearChat(@RequestBody Map<String, Object> body) {
        List<String> participantes = (List<String>) body.get("participantes");
        Chat chat = service.crearChat(participantes);
        return ResponseEntity.ok(chat);
    }

    // 💬 Enviar mensaje
    @PostMapping("/{id}/mensajes")
    public ResponseEntity<Chat> enviarMensaje(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        Chat chat = service.enviarMensaje(id, body.get("remitente"), body.get("contenido"));
        return ResponseEntity.ok(chat);
    }

    // 📜 Listar todos los chats
    @GetMapping
    public ResponseEntity<List<Chat>> listar() {
        return ResponseEntity.ok(service.listarChatsPorUsuario("todos"));
    }

    // 🔍 Obtener chat por ID
    @GetMapping("/{id}")
    public ResponseEntity<Chat> obtener(@PathVariable String id) {
        return ResponseEntity.ok(service.obtenerChat(id));
    }
    @PostMapping("/mensaje")
    public ResponseEntity<Chat> enviarMensajeEntreUsuarios(@RequestBody Map<String, String> body) {
        String remitente = body.get("remitente");
        String destinatario = body.get("destinatario");
        String contenido = body.get("contenido");

        // 🔹 Buscamos si ya existe un chat entre ambos
        List<String> participantes = List.of(remitente, destinatario);
        List<Chat> existentes = service.listarChatsPorUsuario(remitente).stream()
                .filter(c -> c.getParticipantes().containsAll(participantes))
                .toList();

        Chat chat;
        if (existentes.isEmpty()) {
            chat = service.crearChat(participantes);
        } else {
            chat = existentes.get(0);
        }

        // 🔹 Enviamos el mensaje en ese chat
        Chat actualizado = service.enviarMensaje(chat.getId(), remitente, contenido);

        return ResponseEntity.ok(actualizado);
    }
}
