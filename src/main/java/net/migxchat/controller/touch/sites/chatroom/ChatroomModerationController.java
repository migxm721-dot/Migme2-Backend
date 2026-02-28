package net.migxchat.controller.touch.sites.chatroom;

import net.migxchat.dto.request.*;
import net.migxchat.dto.response.BanResponse;
import net.migxchat.dto.response.MemberResponse;
import net.migxchat.dto.response.MuteResponse;
import net.migxchat.model.chatroom.ChatroomBan;
import net.migxchat.model.chatroom.ChatroomMember;
import net.migxchat.model.chatroom.ChatroomMute;
import net.migxchat.service.chatroom.ChatroomModerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sites/touch/chatroom")
public class ChatroomModerationController {

    @Autowired
    private ChatroomModerationService moderationService;

    @PostMapping("/kick")
    public ResponseEntity<?> kickUser(@RequestBody KickUserRequest request) {
        moderationService.kickUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername(), request.getReason());
        return ResponseEntity.ok(Map.of("message", "User kicked successfully"));
    }

    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@RequestBody BanUserRequest request) {
        ChatroomBan ban = moderationService.banUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername(), request.getReason(), request.getDuration());
        return ResponseEntity.ok(BanResponse.from(ban));
    }

    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@RequestBody KickUserRequest request) {
        moderationService.unbanUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername());
        return ResponseEntity.ok(Map.of("message", "User unbanned successfully"));
    }

    @PostMapping("/mute")
    public ResponseEntity<?> muteUser(@RequestBody MuteUserRequest request) {
        ChatroomMute mute = moderationService.muteUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername(), request.getReason(), request.getDuration());
        return ResponseEntity.ok(MuteResponse.from(mute));
    }

    @PostMapping("/unmute")
    public ResponseEntity<?> unmuteUser(@RequestBody KickUserRequest request) {
        moderationService.unmuteUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername());
        return ResponseEntity.ok(Map.of("message", "User unmuted successfully"));
    }

    @PostMapping("/promote")
    public ResponseEntity<?> promoteUser(@RequestBody PromoteUserRequest request) {
        ChatroomMember member = moderationService.promoteUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername(), request.getNewRole());
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @PostMapping("/demote")
    public ResponseEntity<?> demoteUser(@RequestBody PromoteUserRequest request) {
        ChatroomMember member = moderationService.demoteUser(request.getRoomName(), request.getTargetUsername(),
                request.getModeratorUsername(), request.getNewRole());
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @GetMapping("/{roomName}/bans")
    public ResponseEntity<?> getBannedUsers(@PathVariable String roomName) {
        List<BanResponse> bans = moderationService.getActiveBans(roomName).stream()
                .map(BanResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(bans);
    }

    @GetMapping("/{roomName}/mutes")
    public ResponseEntity<?> getMutedUsers(@PathVariable String roomName) {
        List<MuteResponse> mutes = moderationService.getActiveMutes(roomName).stream()
                .map(MuteResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(mutes);
    }

    @GetMapping("/{roomName}/check-ban/{username}")
    public ResponseEntity<?> checkBanStatus(@PathVariable String roomName, @PathVariable String username) {
        boolean isBanned = moderationService.isUserBanned(roomName, username);
        return ResponseEntity.ok(Map.of("roomName", roomName, "username", username, "isBanned", isBanned));
    }

    @GetMapping("/{roomName}/check-mute/{username}")
    public ResponseEntity<?> checkMuteStatus(@PathVariable String roomName, @PathVariable String username) {
        boolean isMuted = moderationService.isUserMuted(roomName, username);
        return ResponseEntity.ok(Map.of("roomName", roomName, "username", username, "isMuted", isMuted));
    }
}
