package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.chatroom.Chatroom;

import java.time.LocalDateTime;

@Data
public class ChatroomResponse {
    private Long id;
    private String roomName;
    private String topic;
    private String description;
    private String ownerUsername;
    private Integer maxMembers;
    private Integer currentMembers;
    private Boolean isPublic;
    private Boolean isPasswordProtected;
    private Boolean allowGuestPost;
    private Integer slowModeSeconds;
    private LocalDateTime createdAt;

    public static ChatroomResponse from(Chatroom chatroom) {
        ChatroomResponse r = new ChatroomResponse();
        r.setId(chatroom.getId());
        r.setRoomName(chatroom.getRoomName());
        r.setTopic(chatroom.getTopic());
        r.setDescription(chatroom.getDescription());
        r.setOwnerUsername(chatroom.getOwnerUsername());
        r.setMaxMembers(chatroom.getMaxMembers());
        r.setCurrentMembers(chatroom.getCurrentMembers());
        r.setIsPublic(chatroom.getIsPublic());
        r.setIsPasswordProtected(chatroom.getIsPasswordProtected());
        r.setAllowGuestPost(chatroom.getAllowGuestPost());
        r.setSlowModeSeconds(chatroom.getSlowModeSeconds());
        r.setCreatedAt(chatroom.getCreatedAt());
        return r;
    }
}
