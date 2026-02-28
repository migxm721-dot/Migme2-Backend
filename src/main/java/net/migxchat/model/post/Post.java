package net.migxchat.model.post;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false, length = 50)
    private String authorId;

    @Column(name = "author_username", nullable = false, length = 50)
    private String authorUsername;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PostType type = PostType.TEXT;

    @Column(length = 20)
    private String status = "active";

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PostPrivacy privacy = PostPrivacy.PUBLIC;

    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Column(name = "video_url", length = 512)
    private String videoUrl;

    @Column(name = "link_url", length = 512)
    private String linkUrl;

    @Column(length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime timestamp;

    @Column(name = "root_post_id")
    private Long rootPostId;

    @Column(name = "parent_post_id")
    private Long parentPostId;

    @Column(name = "reply_count")
    private Integer replyCount = 0;

    @Column(name = "reshare_count")
    private Integer reshareCount = 0;

    @Column(name = "watch_count")
    private Integer watchCount = 0;

    @Column(name = "is_watched")
    private Boolean isWatched = false;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "group_id")
    private Long groupId;
}
