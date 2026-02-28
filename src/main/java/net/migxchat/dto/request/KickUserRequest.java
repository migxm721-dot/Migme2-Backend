package net.migxchat.dto.request;

import lombok.Data;

@Data
public class KickUserRequest {
    private String roomName;
    private String targetUsername;
    private String moderatorUsername;
    private String reason;
}
