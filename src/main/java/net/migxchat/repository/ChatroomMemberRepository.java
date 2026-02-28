package net.migxchat.repository;

import net.migxchat.model.chatroom.ChatroomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Long> {

    @Query("SELECT m FROM ChatroomMember m WHERE m.chatroom.roomName = :roomName AND m.username = :username")
    Optional<ChatroomMember> findByRoomNameAndUsername(@Param("roomName") String roomName,
                                                        @Param("username") String username);

    @Query("SELECT m FROM ChatroomMember m WHERE m.chatroom.roomName = :roomName")
    List<ChatroomMember> findByRoomName(@Param("roomName") String roomName);

    @Query("SELECT COUNT(m) FROM ChatroomMember m WHERE m.chatroom.roomName = :roomName AND m.isOnline = true")
    long countOnlineMembersByRoomName(@Param("roomName") String roomName);
}
