package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.chatroom.ChatroomMute;

import java.time.LocalDateTime;

@Data
public class MuteResponse {
    private Long id;
    private String mutedUsername;
    private String mutedByUsername;
    private String reason;
    private LocalDateTime mutedAt;
    private LocalDateTime expiresAt;
    private Boolean isPermanent;

    public static MuteResponse from(ChatroomMute mute) {
        MuteResponse r = new MuteResponse();
        r.setId(mute.getId());
        r.setMutedUsername(mute.getMutedUsername());
        r.setMutedByUsername(mute.getMutedByUsername());
        r.setReason(mute.getReason());
        r.setMutedAt(mute.getMutedAt());
        r.setExpiresAt(mute.getExpiresAt());
        r.setIsPermanent(mute.getIsPermanent());
        return r;
    }
}
