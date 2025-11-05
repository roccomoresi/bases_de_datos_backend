package com.example.persistencia.poliglota.controller.chat;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Guarda sesiones activas WebSocket
    private final Map<String, WebSocketSession> sesiones = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sesiones.put(session.getId(), session);
        System.out.println("🔌 Nueva conexión WebSocket: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // retransmite el mensaje a todos los conectados (broadcast)
        for (WebSocketSession ses : sesiones.values()) {
            if (ses.isOpen()) {
                ses.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sesiones.remove(session.getId());
        System.out.println("❌ WebSocket desconectado: " + session.getId());
    }
}
