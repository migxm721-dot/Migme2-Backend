package net.migxchat.dto.request;

import lombok.Data;

@Data
public class CreateChatroomRequest {
    private String roomName;
    private String topic;
    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
    private Boolean isPasswordProtected;
    private String roomPassword;
    private Boolean allowGuestPost;
    private Integer slowModeSeconds;
}
