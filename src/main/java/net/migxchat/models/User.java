package net.migxchat.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(length = 100)
    private String email;

    @Column(name = "status_message", columnDefinition = "TEXT")
    private String statusMessage;

    @Column(name = "display_pic_guid", length = 100)
    private String displayPicGuid;

    @Column(name = "avatar_pic_guid", length = 100)
    private String avatarPicGuid;

    @Column
    private Short presence = 0;

    @Column(name = "account_balance", precision = 10, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @Column(name = "mig_level")
    private Integer migLevel = 0;

    @Column(name = "mig_level_image_url", length = 255)
    private String migLevelImageUrl;

    @Column(name = "contact_list_version")
    private Integer contactListVersion = 0;

    @Column(name = "contact_list_timestamp")
    private Long contactListTimestamp = 0L;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
