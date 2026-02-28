package net.migxchat.dto.request;

import lombok.Data;

@Data
public class JoinChatroomRequest {
    private String roomName;
    private String username;
    private String password;
}
