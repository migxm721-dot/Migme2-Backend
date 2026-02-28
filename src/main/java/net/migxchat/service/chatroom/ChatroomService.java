package net.migxchat.service.chatroom;

import net.migxchat.exception.*;
import net.migxchat.model.chatroom.*;
import net.migxchat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatroomService {

    private static final Logger log = LoggerFactory.getLogger(ChatroomService.class);

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatroomMemberRepository memberRepository;

    @Autowired
    private ChatroomBanRepository banRepository;

    @Transactional
    public Chatroom createChatroom(Chatroom chatroom, String ownerUsername) {
        if (chatroomRepository.existsByRoomName(chatroom.getRoomName())) {
            throw new IllegalArgumentException("Chatroom already exists: " + chatroom.getRoomName());
        }
        chatroom.setOwnerUsername(ownerUsername);
        chatroom.setCurrentMembers(0);
        chatroom.setIsActive(true);
        chatroom.setIsDeleted(false);
        Chatroom saved = chatroomRepository.save(chatroom);

        ChatroomMember ownerMember = new ChatroomMember();
        ownerMember.setChatroom(saved);
        ownerMember.setUsername(ownerUsername);
        ownerMember.setRole(ChatroomRole.OWNER);
        ownerMember.setIsOnline(true);
        memberRepository.save(ownerMember);

        saved.setCurrentMembers(1);
        return chatroomRepository.save(saved);
    }

    @Transactional
    public ChatroomMember joinChatroom(String roomName, String username, String password) {
        Chatroom chatroom = getChatroom(roomName);

        if (chatroom.isFull()) {
            throw new ChatroomFullException("Chatroom is full: " + roomName);
        }

        if (Boolean.TRUE.equals(chatroom.getIsPasswordProtected())) {
            if (password == null || !password.equals(chatroom.getRoomPassword())) {
                throw new UnauthorizedActionException("Invalid chatroom password");
            }
        }

        boolean isBanned = banRepository.findActiveBanByRoomAndUser(chatroom.getId(), username, LocalDateTime.now())
            .isPresent();
        if (isBanned) {
            throw new UserBannedException("User " + username + " is banned from " + roomName);
        }

        if (memberRepository.findByRoomNameAndUsername(roomName, username).isPresent()) {
            return memberRepository.findByRoomNameAndUsername(roomName, username).get();
        }

        ChatroomMember member = new ChatroomMember();
        member.setChatroom(chatroom);
        member.setUsername(username);
        member.setRole(ChatroomRole.MEMBER);
        member.setIsOnline(true);
        ChatroomMember saved = memberRepository.save(member);

        chatroom.incrementMemberCount();
        chatroomRepository.save(chatroom);

        return saved;
    }

    @Transactional
    public void leaveChatroom(String roomName, String username) {
        Chatroom chatroom = getChatroom(roomName);
        memberRepository.findByRoomNameAndUsername(roomName, username).ifPresent(member -> {
            memberRepository.delete(member);
            chatroom.decrementMemberCount();
            chatroomRepository.save(chatroom);
        });
    }

    @Transactional
    public void deleteChatroom(String roomName, String username) {
        Chatroom chatroom = getChatroom(roomName);
        if (!username.equals(chatroom.getOwnerUsername())) {
            throw new UnauthorizedActionException("Only the owner can delete the chatroom");
        }
        chatroom.setIsDeleted(true);
        chatroom.setIsActive(false);
        chatroomRepository.save(chatroom);
    }

    @Transactional
    public Chatroom updateChatroom(String roomName, Chatroom updates, String username) {
        Chatroom chatroom = getChatroom(roomName);
        ChatroomMember member = memberRepository.findByRoomNameAndUsername(roomName, username)
            .orElseThrow(() -> new UnauthorizedActionException("You are not a member of this chatroom"));

        if (!member.getRole().isCanManageRoles() && !username.equals(chatroom.getOwnerUsername())) {
            throw new UnauthorizedActionException("You don't have permission to update chatroom settings");
        }

        if (updates.getTopic() != null) chatroom.setTopic(updates.getTopic());
        if (updates.getDescription() != null) chatroom.setDescription(updates.getDescription());
        if (updates.getMaxMembers() != null) chatroom.setMaxMembers(updates.getMaxMembers());
        if (updates.getIsPublic() != null) chatroom.setIsPublic(updates.getIsPublic());
        if (updates.getIsPasswordProtected() != null) chatroom.setIsPasswordProtected(updates.getIsPasswordProtected());
        if (updates.getRoomPassword() != null) chatroom.setRoomPassword(updates.getRoomPassword());
        if (updates.getAllowGuestPost() != null) chatroom.setAllowGuestPost(updates.getAllowGuestPost());
        if (updates.getSlowModeSeconds() != null) chatroom.setSlowModeSeconds(updates.getSlowModeSeconds());

        return chatroomRepository.save(chatroom);
    }

    public Chatroom getChatroom(String roomName) {
        return chatroomRepository.findByRoomName(roomName)
            .orElseThrow(() -> new ChatroomNotFoundException("Chatroom not found: " + roomName));
    }

    public Page<Chatroom> searchChatrooms(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return chatroomRepository.findByIsPublicTrueAndIsActiveTrueAndIsDeletedFalse(pageable);
        }
        return chatroomRepository.searchByNameOrTopic(query, pageable);
    }

    public Page<Chatroom> getPublicChatrooms(Pageable pageable) {
        return chatroomRepository.findByIsPublicTrueAndIsActiveTrueAndIsDeletedFalse(pageable);
    }

    public List<ChatroomMember> getChatroomMembers(String roomName) {
        getChatroom(roomName);
        return memberRepository.findByRoomName(roomName);
    }

    @Transactional
    public Chatroom transferOwnership(String roomName, String currentOwner, String newOwner) {
        Chatroom chatroom = getChatroom(roomName);
        if (!currentOwner.equals(chatroom.getOwnerUsername())) {
            throw new UnauthorizedActionException("Only the current owner can transfer ownership");
        }

        ChatroomMember newOwnerMember = memberRepository.findByRoomNameAndUsername(roomName, newOwner)
            .orElseThrow(() -> new UserNotFoundException("New owner is not a member of the chatroom"));

        memberRepository.findByRoomNameAndUsername(roomName, currentOwner).ifPresent(m -> {
            m.setRole(ChatroomRole.ADMIN);
            memberRepository.save(m);
        });

        newOwnerMember.setRole(ChatroomRole.OWNER);
        memberRepository.save(newOwnerMember);

        chatroom.setOwnerUsername(newOwner);
        return chatroomRepository.save(chatroom);
    }
}
