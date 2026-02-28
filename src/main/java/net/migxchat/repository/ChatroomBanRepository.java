package net.migxchat.repository;

import net.migxchat.model.chatroom.ChatroomBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomBanRepository extends JpaRepository<ChatroomBan, Long> {

    @Query("SELECT b FROM ChatroomBan b WHERE b.chatroom.id = :chatroomId " +
           "AND b.bannedUsername = :username AND b.isActive = true " +
           "AND (b.isPermanent = true OR b.expiresAt > :now)")
    Optional<ChatroomBan> findActiveBanByRoomAndUser(@Param("chatroomId") Long chatroomId,
                                                      @Param("username") String username,
                                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM ChatroomBan b WHERE b.chatroom.id = :chatroomId AND b.isActive = true " +
           "AND (b.isPermanent = true OR b.expiresAt > :now)")
    List<ChatroomBan> findActiveBansByChatroom(@Param("chatroomId") Long chatroomId,
                                                @Param("now") LocalDateTime now);

    @Query("SELECT b FROM ChatroomBan b WHERE b.isActive = true AND b.isPermanent = false " +
           "AND b.expiresAt <= :now")
    List<ChatroomBan> findExpiredBans(@Param("now") LocalDateTime now);
}
