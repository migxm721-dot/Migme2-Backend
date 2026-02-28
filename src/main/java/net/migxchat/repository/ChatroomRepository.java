package net.migxchat.repository;

import net.migxchat.model.chatroom.Chatroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    Optional<Chatroom> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);

    Page<Chatroom> findByIsPublicTrueAndIsActiveTrueAndIsDeletedFalse(Pageable pageable);

    @Query("SELECT c FROM Chatroom c WHERE c.isDeleted = false AND c.isActive = true " +
           "AND (LOWER(c.roomName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.topic) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Chatroom> searchByNameOrTopic(@Param("query") String query, Pageable pageable);
}
