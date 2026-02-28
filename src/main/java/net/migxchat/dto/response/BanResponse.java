package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.chatroom.ChatroomBan;

import java.time.LocalDateTime;

@Data
public class BanResponse {
    private Long id;
    private String bannedUsername;
    private String bannedByUsername;
    private String reason;
    private LocalDateTime bannedAt;
    private LocalDateTime expiresAt;
    private Boolean isPermanent;

    public static BanResponse from(ChatroomBan ban) {
        BanResponse r = new BanResponse();
        r.setId(ban.getId());
        r.setBannedUsername(ban.getBannedUsername());
        r.setBannedByUsername(ban.getBannedByUsername());
        r.setReason(ban.getReason());
        r.setBannedAt(ban.getBannedAt());
        r.setExpiresAt(ban.getExpiresAt());
        r.setIsPermanent(ban.getIsPermanent());
        return r;
    }
}
