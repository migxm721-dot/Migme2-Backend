package net.migxchat.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false, length = 100)
    private String messageId;

    @Column(name = "message_type", nullable = false)
    private Short messageType;

    @Column(name = "source_username", nullable = false, length = 50)
    private String sourceUsername;

    @Column(name = "destination_type", nullable = false)
    private Short destinationType;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "content_type", nullable = false)
    private Short contentType;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(length = 255)
    private String filename;

    @Column(name = "display_pic_guid", length = 100)
    private String displayPicGuid;

    @Column(nullable = false)
    private Long timestamp;

    @Column(name = "delivery_status")
    private Short deliveryStatus = 0;

    @Column(name = "message_direction")
    private Short messageDirection = 0;

    @Column(name = "is_server_info")
    private Boolean isServerInfo = false;

    @Column(name = "pinned_type")
    private Short pinnedType = 0;

    @Column(name = "emote_content_type")
    private Short emoteContentType = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
