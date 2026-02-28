package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostPrivacy;
import net.migxchat.model.post.PostType;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private Long id;
    private String authorId;
    private String authorUsername;
    private String body;
    private PostType type;
    private PostPrivacy privacy;
    private String photoUrl;
    private String videoUrl;
    private String linkUrl;
    private String location;
    private String tags;
    private LocalDateTime timestamp;
    private Long rootPostId;
    private Long parentPostId;
    private Integer replyCount;
    private Integer reshareCount;
    private Integer watchCount;
    private Boolean isWatched;
    private Boolean isLocked;
    private Integer tagId;

    public static PostResponse from(Post post) {
        PostResponse r = new PostResponse();
        r.setId(post.getId());
        r.setAuthorId(post.getAuthorId());
        r.setAuthorUsername(post.getAuthorUsername());
        r.setBody(post.getBody());
        r.setType(post.getType());
        r.setPrivacy(post.getPrivacy());
        r.setPhotoUrl(post.getPhotoUrl());
        r.setVideoUrl(post.getVideoUrl());
        r.setLinkUrl(post.getLinkUrl());
        r.setLocation(post.getLocation());
        r.setTags(post.getTags());
        r.setTimestamp(post.getTimestamp());
        r.setRootPostId(post.getRootPostId());
        r.setParentPostId(post.getParentPostId());
        r.setReplyCount(post.getReplyCount());
        r.setReshareCount(post.getReshareCount());
        r.setWatchCount(post.getWatchCount());
        r.setIsWatched(post.getIsWatched());
        r.setIsLocked(post.getIsLocked());
        r.setTagId(post.getTagId());
        return r;
    }
}
