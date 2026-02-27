package net.migxchat.services;

import net.migxchat.models.Session;
import net.migxchat.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(String username, String ipAddress, String deviceId, String channelId) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUsername(username);
        session.setIpAddress(ipAddress);
        session.setDeviceId(deviceId);
        session.setChannelId(channelId);
        session.setLastActivity(LocalDateTime.now());
        session.setIsActive(true);
        return sessionRepository.save(session);
    }

    public Optional<Session> findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void invalidateSession(String sessionId) {
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setIsActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void invalidateAllUserSessions(String username) {
        List<Session> sessions = sessionRepository.findByUsernameAndIsActiveTrue(username);
        sessions.forEach(s -> s.setIsActive(false));
        sessionRepository.saveAll(sessions);
    }

    @Transactional
    public void updateLastActivity(String sessionId) {
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setLastActivity(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }
}
