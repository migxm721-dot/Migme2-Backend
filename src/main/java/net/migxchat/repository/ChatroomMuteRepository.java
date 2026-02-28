package net.migxchat.repository;

import net.migxchat.model.chatroom.ChatroomMute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomMuteRepository extends JpaRepository<ChatroomMute, Long> {

    @Query("SELECT m FROM ChatroomMute m WHERE m.chatroom.id = :chatroomId " +
           "AND m.mutedUsername = :username AND m.isActive = true " +
           "AND (m.isPermanent = true OR m.expiresAt > :now)")
    Optional<ChatroomMute> findActiveMuteByRoomAndUser(@Param("chatroomId") Long chatroomId,
                                                        @Param("username") String username,
                                                        @Param("now") LocalDateTime now);

    @Query("SELECT m FROM ChatroomMute m WHERE m.chatroom.id = :chatroomId AND m.isActive = true " +
           "AND (m.isPermanent = true OR m.expiresAt > :now)")
    List<ChatroomMute> findActiveMutesByChatroom(@Param("chatroomId") Long chatroomId,
                                                  @Param("now") LocalDateTime now);

    @Query("SELECT m FROM ChatroomMute m WHERE m.isActive = true AND m.isPermanent = false " +
           "AND m.expiresAt <= :now")
    List<ChatroomMute> findExpiredMutes(@Param("now") LocalDateTime now);
}
