package net.migxchat.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_username", nullable = false, length = 50)
    private String userUsername;

    @Column(name = "contact_username", nullable = false, length = 50)
    private String contactUsername;

    @Column(name = "contact_id")
    private Integer contactId;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
