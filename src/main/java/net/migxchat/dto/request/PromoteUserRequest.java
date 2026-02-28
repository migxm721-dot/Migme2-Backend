package net.migxchat.dto.request;

import lombok.Data;
import net.migxchat.model.chatroom.ChatroomRole;

@Data
public class PromoteUserRequest {
    private String roomName;
    private String targetUsername;
    private String moderatorUsername;
    private ChatroomRole newRole;
}
