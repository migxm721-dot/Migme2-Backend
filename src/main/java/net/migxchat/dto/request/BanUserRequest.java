package net.migxchat.dto.request;

import lombok.Data;
import net.migxchat.model.chatroom.BanDuration;

@Data
public class BanUserRequest {
    private String roomName;
    private String targetUsername;
    private String moderatorUsername;
    private String reason;
    private BanDuration duration;
}
