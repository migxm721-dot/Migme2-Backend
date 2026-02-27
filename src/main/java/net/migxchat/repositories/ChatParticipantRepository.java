package net.migxchat.repositories;

import net.migxchat.models.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatIdAndIsActiveTrue(String chatId);
    Optional<ChatParticipant> findByChatIdAndUsername(String chatId, String username);
    List<ChatParticipant> findByUsernameAndIsActiveTrue(String username);
}
