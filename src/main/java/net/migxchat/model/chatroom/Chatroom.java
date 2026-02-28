package net.migxchat.model.chatroom;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "chatrooms")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", unique = true, nullable = false, length = 100)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_username", nullable = false, length = 50)
    private String ownerUsername;

    @Column(name = "max_members")
    private Integer maxMembers = 100;

    @Column(name = "current_members")
    private Integer currentMembers = 0;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "is_password_protected")
    private Boolean isPasswordProtected = false;

    @Column(name = "room_password", length = 255)
    private String roomPassword;

    @Column(name = "allow_guest_post")
    private Boolean allowGuestPost = true;

    @Column(name = "slow_mode_seconds")
    private Integer slowModeSeconds = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatroomMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatroomBan> bans = new ArrayList<>();

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatroomMute> mutes = new ArrayList<>();

    public boolean isFull() {
        return currentMembers >= maxMembers;
    }

    public void incrementMemberCount() {
        this.currentMembers = (this.currentMembers == null ? 0 : this.currentMembers) + 1;
    }

    public void decrementMemberCount() {
        if (this.currentMembers != null && this.currentMembers > 0) {
            this.currentMembers--;
        }
    }
}
