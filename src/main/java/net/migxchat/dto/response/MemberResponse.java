package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.chatroom.ChatroomMember;
import net.migxchat.model.chatroom.ChatroomRole;

import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private Long id;
    private String username;
    private ChatroomRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastSeenAt;
    private Boolean isOnline;
    private Long messageCount;

    public static MemberResponse from(ChatroomMember member) {
        MemberResponse r = new MemberResponse();
        r.setId(member.getId());
        r.setUsername(member.getUsername());
        r.setRole(member.getRole());
        r.setJoinedAt(member.getJoinedAt());
        r.setLastSeenAt(member.getLastSeenAt());
        r.setIsOnline(member.getIsOnline());
        r.setMessageCount(member.getMessageCount());
        return r;
    }
}
