package net.migxchat.controllers;

import net.migxchat.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String guid = UUID.randomUUID().toString();
        log.info("Received image upload: {} -> {}", file.getOriginalFilename(), guid);

        return ResponseEntity.ok(Map.of(
                "guid", guid,
                "filename", file.getOriginalFilename(),
                "size", file.getSize()
        ));
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
