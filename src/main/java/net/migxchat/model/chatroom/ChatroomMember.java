package net.migxchat.model.chatroom;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chatroom_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"chatroom_id", "username"}))
public class ChatroomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(nullable = false, length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatroomRole role = ChatroomRole.MEMBER;

    @CreationTimestamp
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "message_count")
    private Long messageCount = 0L;

    public boolean isOwner() { return ChatroomRole.OWNER.equals(role); }
    public boolean isAdmin() { return ChatroomRole.ADMIN.equals(role); }
    public boolean isModerator() { return ChatroomRole.MODERATOR.equals(role); }
    public boolean canKick() { return role != null && role.isCanKick(); }
    public boolean canBan() { return role != null && role.isCanBan(); }
    public boolean canMute() { return role != null && role.isCanMute(); }
}
