package net.migxchat.dto.request;

import lombok.Data;

@Data
public class UpdateChatroomRequest {
    private String topic;
    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
    private Boolean isPasswordProtected;
    private String roomPassword;
    private Boolean allowGuestPost;
    private Integer slowModeSeconds;
}
