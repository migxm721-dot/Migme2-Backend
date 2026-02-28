package net.migxchat.dto.request;

import lombok.Data;
import net.migxchat.model.post.PostPrivacy;
import net.migxchat.model.post.PostType;

@Data
public class CreatePostRequest {
    private String body;
    private PostType type;
    private PostPrivacy privacy;
    private String photoUrl;
    private String videoUrl;
    private String linkUrl;
    private String location;
    private String tags;
    private Long parentPostId;
    private Long rootPostId;
    private Long groupId;
}
