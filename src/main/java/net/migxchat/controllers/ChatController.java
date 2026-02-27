package net.migxchat.controllers;

import net.migxchat.models.Message;
import net.migxchat.services.MessageService;
import net.migxchat.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/history/{destination}")
    public ResponseEntity<?> getHistory(@PathVariable String destination,
                                         @RequestParam(defaultValue = "50") int limit,
                                         @RequestParam(defaultValue = "0") long before,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        List<Message> messages = messageService.getMessageHistory(destination, limit, before);
        return ResponseEntity.ok(messages);
    }

    private boolean isAuthenticated(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        try {
            return jwtUtils.extractUsername(authHeader.substring(7)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
