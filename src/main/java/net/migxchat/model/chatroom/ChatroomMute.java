package net.migxchat.model.chatroom;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chatroom_mutes")
public class ChatroomMute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(name = "muted_username", nullable = false, length = 50)
    private String mutedUsername;

    @Column(name = "muted_by_username", nullable = false, length = 50)
    private String mutedByUsername;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "muted_at")
    private LocalDateTime mutedAt;

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

    public void unmute() {
        this.isActive = false;
    }
}
