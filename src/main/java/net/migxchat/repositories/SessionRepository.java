package net.migxchat.repositories;

import net.migxchat.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);
    List<Session> findByUsernameAndIsActiveTrue(String username);
    void deleteBySessionId(String sessionId);
}
