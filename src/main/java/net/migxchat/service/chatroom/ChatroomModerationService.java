package net.migxchat.service.chatroom;

import net.migxchat.exception.*;
import net.migxchat.model.chatroom.*;
import net.migxchat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatroomModerationService {

    private static final Logger log = LoggerFactory.getLogger(ChatroomModerationService.class);

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatroomMemberRepository memberRepository;

    @Autowired
    private ChatroomBanRepository banRepository;

    @Autowired
    private ChatroomMuteRepository muteRepository;

    @Transactional
    public void kickUser(String roomName, String targetUsername, String moderatorUsername, String reason) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);
        ChatroomMember target = getMember(roomName, targetUsername);

        if (!moderator.canKick()) {
            throw new UnauthorizedActionException("You don't have permission to kick users");
        }
        if (moderator.getRole().isHigherThan(target.getRole()) || target.isOwner()) {
            throw new UnauthorizedActionException("Cannot kick a user with equal or higher role");
        }

        memberRepository.delete(target);
        chatroom.decrementMemberCount();
        chatroomRepository.save(chatroom);
        log.info("User {} kicked from {} by {} for: {}", targetUsername, roomName, moderatorUsername, reason);
    }

    @Transactional
    public ChatroomBan banUser(String roomName, String targetUsername, String moderatorUsername,
                               String reason, BanDuration duration) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);

        if (!moderator.canBan()) {
            throw new UnauthorizedActionException("You don't have permission to ban users");
        }

        memberRepository.findByRoomNameAndUsername(roomName, targetUsername).ifPresent(target -> {
            if (!moderator.getRole().isHigherThan(target.getRole())) {
                throw new UnauthorizedActionException("Cannot ban a user with equal or higher role");
            }
            memberRepository.delete(target);
            chatroom.decrementMemberCount();
            chatroomRepository.save(chatroom);
        });

        ChatroomBan ban = new ChatroomBan();
        ban.setChatroom(chatroom);
        ban.setBannedUsername(targetUsername);
        ban.setBannedByUsername(moderatorUsername);
        ban.setReason(reason);
        ban.setIsActive(true);

        if (duration == null || duration.isPermanent()) {
            ban.setIsPermanent(true);
        } else {
            ban.setIsPermanent(false);
            ban.setExpiresAt(LocalDateTime.now().plusSeconds(duration.getSeconds()));
        }

        return banRepository.save(ban);
    }

    @Transactional
    public void unbanUser(String roomName, String targetUsername, String moderatorUsername) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);

        if (!moderator.canBan()) {
            throw new UnauthorizedActionException("You don't have permission to unban users");
        }

        List<ChatroomBan> bans = banRepository.findActiveBansByChatroom(chatroom.getId(), LocalDateTime.now());
        bans.stream()
            .filter(b -> b.getBannedUsername().equals(targetUsername))
            .forEach(b -> {
                b.expire();
                banRepository.save(b);
            });
    }

    @Transactional
    public ChatroomMute muteUser(String roomName, String targetUsername, String moderatorUsername,
                                  String reason, BanDuration duration) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);

        if (!moderator.canMute()) {
            throw new UnauthorizedActionException("You don't have permission to mute users");
        }

        ChatroomMute mute = new ChatroomMute();
        mute.setChatroom(chatroom);
        mute.setMutedUsername(targetUsername);
        mute.setMutedByUsername(moderatorUsername);
        mute.setReason(reason);
        mute.setIsActive(true);

        if (duration == null || duration.isPermanent()) {
            mute.setIsPermanent(true);
        } else {
            mute.setIsPermanent(false);
            mute.setExpiresAt(LocalDateTime.now().plusSeconds(duration.getSeconds()));
        }

        return muteRepository.save(mute);
    }

    @Transactional
    public void unmuteUser(String roomName, String targetUsername, String moderatorUsername) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);

        if (!moderator.canMute()) {
            throw new UnauthorizedActionException("You don't have permission to unmute users");
        }

        List<ChatroomMute> mutes = muteRepository.findActiveMutesByChatroom(chatroom.getId(), LocalDateTime.now());
        mutes.stream()
            .filter(m -> m.getMutedUsername().equals(targetUsername))
            .forEach(m -> {
                m.unmute();
                muteRepository.save(m);
            });
    }

    @Transactional
    public ChatroomMember promoteUser(String roomName, String targetUsername, String moderatorUsername,
                                       ChatroomRole newRole) {
        getMember(roomName, moderatorUsername);
        ChatroomMember target = getMember(roomName, targetUsername);
        ChatroomMember moderator = getMember(roomName, moderatorUsername);

        if (!moderator.getRole().isCanManageRoles()) {
            throw new UnauthorizedActionException("You don't have permission to manage roles");
        }
        if (!moderator.getRole().isHigherThan(newRole)) {
            throw new UnauthorizedActionException("Cannot assign a role equal to or higher than your own");
        }

        target.setRole(newRole);
        return memberRepository.save(target);
    }

    @Transactional
    public ChatroomMember demoteUser(String roomName, String targetUsername, String moderatorUsername,
                                      ChatroomRole newRole) {
        return promoteUser(roomName, targetUsername, moderatorUsername, newRole);
    }

    public boolean isUserBanned(String roomName, String username) {
        return chatroomRepository.findByRoomName(roomName)
            .map(c -> banRepository.findActiveBanByRoomAndUser(c.getId(), username, LocalDateTime.now()).isPresent())
            .orElse(false);
    }

    public boolean isUserMuted(String roomName, String username) {
        return chatroomRepository.findByRoomName(roomName)
            .map(c -> muteRepository.findActiveMuteByRoomAndUser(c.getId(), username, LocalDateTime.now()).isPresent())
            .orElse(false);
    }

    public List<ChatroomBan> getActiveBans(String roomName) {
        Chatroom chatroom = getChatroom(roomName);
        return banRepository.findActiveBansByChatroom(chatroom.getId(), LocalDateTime.now());
    }

    public List<ChatroomMute> getActiveMutes(String roomName) {
        Chatroom chatroom = getChatroom(roomName);
        return muteRepository.findActiveMutesByChatroom(chatroom.getId(), LocalDateTime.now());
    }

    @Transactional
    @Scheduled(fixedRateString = "${app.moderation.cleanup-interval:3600000}")
    public void cleanupExpiredModeration() {
        LocalDateTime now = LocalDateTime.now();
        List<ChatroomBan> expiredBans = banRepository.findExpiredBans(now);
        expiredBans.forEach(b -> {
            b.expire();
            banRepository.save(b);
        });
        List<ChatroomMute> expiredMutes = muteRepository.findExpiredMutes(now);
        expiredMutes.forEach(m -> {
            m.unmute();
            muteRepository.save(m);
        });
        if (!expiredBans.isEmpty() || !expiredMutes.isEmpty()) {
            log.info("Cleaned up {} expired bans and {} expired mutes", expiredBans.size(), expiredMutes.size());
        }
    }

    private Chatroom getChatroom(String roomName) {
        return chatroomRepository.findByRoomName(roomName)
            .orElseThrow(() -> new ChatroomNotFoundException("Chatroom not found: " + roomName));
    }

    private ChatroomMember getMember(String roomName, String username) {
        return memberRepository.findByRoomNameAndUsername(roomName, username)
            .orElseThrow(() -> new UserNotFoundException("User " + username + " is not a member of " + roomName));
    }
}
