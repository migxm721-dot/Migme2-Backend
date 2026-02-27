package net.migxchat.services;

import net.migxchat.models.User;
import net.migxchat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Transactional
    public User createUser(String username, String password, String email, String displayName) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setDisplayName(displayName != null ? displayName : username);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePresence(String username, short presence) {
        return userRepository.findByUsername(username).map(user -> {
            user.setPresence(presence);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional
    public User updateLastLogin(String username) {
        return userRepository.findByUsername(username).map(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}
