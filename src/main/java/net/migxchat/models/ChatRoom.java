package net.migxchat.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true, nullable = false, length = 100)
    private String chatId;

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "creator_username", length = 50)
    private String creatorUsername;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "max_participants")
    private Integer maxParticipants = 100;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
