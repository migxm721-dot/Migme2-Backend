package net.migxchat.controller.touch.sites.chatroom;

import net.migxchat.dto.request.*;
import net.migxchat.dto.response.ChatroomResponse;
import net.migxchat.dto.response.MemberResponse;
import net.migxchat.model.chatroom.Chatroom;
import net.migxchat.model.chatroom.ChatroomMember;
import net.migxchat.service.chatroom.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sites/touch/chatroom")
public class ChatroomController {

    @Autowired
    private ChatroomService chatroomService;

    @PostMapping("/create")
    public ResponseEntity<?> createChatroom(@RequestBody CreateChatroomRequest request,
                                             @RequestHeader(value = "X-Username", required = false) String username) {
        Chatroom chatroom = new Chatroom();
        chatroom.setRoomName(request.getRoomName());
        chatroom.setTopic(request.getTopic());
        chatroom.setDescription(request.getDescription());
        if (request.getMaxMembers() != null) chatroom.setMaxMembers(request.getMaxMembers());
        if (request.getIsPublic() != null) chatroom.setIsPublic(request.getIsPublic());
        if (request.getIsPasswordProtected() != null) chatroom.setIsPasswordProtected(request.getIsPasswordProtected());
        if (request.getRoomPassword() != null) chatroom.setRoomPassword(request.getRoomPassword());
        if (request.getAllowGuestPost() != null) chatroom.setAllowGuestPost(request.getAllowGuestPost());
        if (request.getSlowModeSeconds() != null) chatroom.setSlowModeSeconds(request.getSlowModeSeconds());

        Chatroom created = chatroomService.createChatroom(chatroom, username != null ? username : "unknown");
        return ResponseEntity.ok(ChatroomResponse.from(created));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinChatroom(@RequestBody JoinChatroomRequest request) {
        ChatroomMember member = chatroomService.joinChatroom(
                request.getRoomName(), request.getUsername(), request.getPassword());
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveChatroom(@RequestBody Map<String, String> body) {
        chatroomService.leaveChatroom(body.get("roomName"), body.get("username"));
        return ResponseEntity.ok(Map.of("message", "Left chatroom successfully"));
    }

    @GetMapping("/setup")
    public ResponseEntity<?> getChatroom(@RequestParam String roomName) {
        Chatroom chatroom = chatroomService.getChatroom(roomName);
        return ResponseEntity.ok(ChatroomResponse.from(chatroom));
    }

    @PostMapping("/update_settings")
    public ResponseEntity<?> updateChatroom(@RequestBody Map<String, Object> body) {
        String roomName = (String) body.get("roomName");
        String username = (String) body.get("username");

        Chatroom updates = new Chatroom();
        if (body.containsKey("topic")) updates.setTopic((String) body.get("topic"));
        if (body.containsKey("description")) updates.setDescription((String) body.get("description"));
        if (body.containsKey("maxMembers")) updates.setMaxMembers((Integer) body.get("maxMembers"));
        if (body.containsKey("isPublic")) updates.setIsPublic((Boolean) body.get("isPublic"));
        if (body.containsKey("isPasswordProtected")) updates.setIsPasswordProtected((Boolean) body.get("isPasswordProtected"));
        if (body.containsKey("roomPassword")) updates.setRoomPassword((String) body.get("roomPassword"));
        if (body.containsKey("allowGuestPost")) updates.setAllowGuestPost((Boolean) body.get("allowGuestPost"));
        if (body.containsKey("slowModeSeconds")) updates.setSlowModeSeconds((Integer) body.get("slowModeSeconds"));

        Chatroom updated = chatroomService.updateChatroom(roomName, updates, username);
        return ResponseEntity.ok(ChatroomResponse.from(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteChatroom(@RequestBody Map<String, String> body) {
        chatroomService.deleteChatroom(body.get("roomName"), body.get("username"));
        return ResponseEntity.ok(Map.of("message", "Chatroom deleted successfully"));
    }

    @GetMapping("/search_submit")
    public ResponseEntity<?> searchChatrooms(@RequestParam String search,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Page<Chatroom> results = chatroomService.searchChatrooms(search, PageRequest.of(page, size));
        List<ChatroomResponse> responses = results.getContent().stream()
                .map(ChatroomResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("rooms", responses, "total", results.getTotalElements(),
                "page", page, "size", size));
    }

    @GetMapping("/list")
    public ResponseEntity<?> listChatrooms(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        Page<Chatroom> results = chatroomService.getPublicChatrooms(PageRequest.of(page, size));
        List<ChatroomResponse> responses = results.getContent().stream()
                .map(ChatroomResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("rooms", responses, "total", results.getTotalElements(),
                "page", page, "size", size));
    }

    @GetMapping("/{roomName}/members")
    public ResponseEntity<?> getMembers(@PathVariable String roomName) {
        List<MemberResponse> members = chatroomService.getChatroomMembers(roomName).stream()
                .map(MemberResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/transfer_ownership")
    public ResponseEntity<?> transferOwnership(@RequestBody Map<String, String> body) {
        Chatroom chatroom = chatroomService.transferOwnership(
                body.get("roomName"), body.get("currentOwner"), body.get("newOwner"));
        return ResponseEntity.ok(ChatroomResponse.from(chatroom));
    }
}
