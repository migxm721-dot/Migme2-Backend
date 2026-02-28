package net.migxchat.model.post;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_mentions")
public class PostMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentioned_user_id", nullable = false, length = 50)
    private String mentionedUserId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "mentioned_by_user_id", nullable = false, length = 50)
    private String mentionedByUserId;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
