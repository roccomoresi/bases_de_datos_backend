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

    // 🟢 Crear chat privado
    @PostMapping("/privado")
    public ResponseEntity<Chat> crearChat(@RequestBody Map<String, Object> body) {
        List<String> participantes = (List<String>) body.get("participantes");
        return ResponseEntity.ok(service.crearChat(participantes));
    }

    // 🟣 Crear grupo
    @PostMapping("/grupo")
    public ResponseEntity<Chat> crearGrupo(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombreGrupo");
        List<String> participantes = (List<String>) body.get("participantes");
        return ResponseEntity.ok(service.crearGrupo(nombre, participantes));
    }

    // 💬 Enviar mensaje
    @PostMapping("/{chatId}/mensajes")
    public ResponseEntity<Chat> enviarMensaje(@PathVariable String chatId, @RequestBody Map<String, String> body) {
        String remitente = body.get("remitente");
        String contenido = body.get("contenido");
        return ResponseEntity.ok(service.enviarMensaje(chatId, remitente, contenido));
    }

    // 🗂️ Ver chat
    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> obtenerChat(@PathVariable String chatId) {
        return ResponseEntity.ok(service.obtenerChat(chatId));
    }

    // 📱 Ver chats por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Chat>> listarPorUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(service.listarChatsPorUsuario(usuarioId));
    }

    // 🕓 Conversaciones recientes
    @GetMapping("/recientes")
    public ResponseEntity<List<Chat>> getRecientes() {
        return ResponseEntity.ok(service.listarRecientes());
    }

    // 👁️ Marcar mensaje como leído
    @PutMapping("/{chatId}/mensajes/{index}/leido")
    public ResponseEntity<Chat> marcarLeido(@PathVariable String chatId, @PathVariable int index) {
        return ResponseEntity.ok(service.marcarMensajeComoLeido(chatId, index));
    }
}

