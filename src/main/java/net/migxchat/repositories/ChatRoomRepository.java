package net.migxchat.repositories;

import net.migxchat.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChatId(String chatId);
    List<ChatRoom> findByIsPublicTrueAndIsActiveTrue();
}
