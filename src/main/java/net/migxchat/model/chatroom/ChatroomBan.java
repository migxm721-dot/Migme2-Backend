package net.migxchat.model.chatroom;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chatroom_bans")
public class ChatroomBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(name = "banned_username", nullable = false, length = 50)
    private String bannedUsername;

    @Column(name = "banned_by_username", nullable = false, length = 50)
    private String bannedByUsername;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_permanent")
    private Boolean isPermanent = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public boolean isExpired() {
        if (Boolean.TRUE.equals(isPermanent)) return false;
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public void expire() {
        this.isActive = false;
    }
}
