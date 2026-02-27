package net.migxchat.services;

import io.netty.channel.ChannelHandlerContext;
import net.migxchat.gateway.ClientConnectionManager;
import net.migxchat.models.ChatParticipant;
import net.migxchat.models.ChatRoom;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketSerializer;
import net.migxchat.protocol.PacketType;
import net.migxchat.repositories.ChatParticipantRepository;
import net.migxchat.repositories.ChatRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    public static final short FIELD_CHAT_ID = 1;
    public static final short FIELD_USERNAME = 2;
    public static final short FIELD_STATUS = 3;
    public static final short FIELD_ROOM_NAME = 4;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatParticipantRepository chatParticipantRepository;

    @Autowired
    private ClientConnectionManager connectionManager;

    @Autowired
    private PacketSerializer packetSerializer;

    @Transactional
    public void joinRoom(String username, String chatId, ChannelHandlerContext ctx, int seqNum) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findByChatId(chatId);
        if (roomOpt.isEmpty()) {
            log.warn("Chat room not found: {}", chatId);
            return;
        }

        Optional<ChatParticipant> existing = chatParticipantRepository.findByChatIdAndUsername(chatId, username);
        if (existing.isPresent()) {
            ChatParticipant p = existing.get();
            p.setIsActive(true);
            p.setLeftAt(null);
            chatParticipantRepository.save(p);
        } else {
            ChatParticipant participant = new ChatParticipant();
            participant.setChatId(chatId);
            participant.setUsername(username);
            participant.setIsActive(true);
            chatParticipantRepository.save(participant);
        }

        // Notify other participants
        List<String> others = getActiveParticipantUsernames(chatId).stream()
                .filter(u -> !u.equals(username))
                .collect(Collectors.toList());

        FusionPacket joinNotif = new FusionPacket(PacketType.CHATROOM_USER_STATUS);
        joinNotif.addField(FIELD_CHAT_ID, chatId);
        joinNotif.addField(FIELD_USERNAME, username);
        joinNotif.addField(FIELD_STATUS, (short) 1);
        connectionManager.broadcastPacket(others, joinNotif);

        // Send confirmation to joiner
        FusionPacket confirm = new FusionPacket(PacketType.JOIN_CHATROOM);
        confirm.setSequenceNumber(seqNum);
        confirm.addField(FIELD_CHAT_ID, chatId);
        confirm.addField(FIELD_STATUS, (short) 1);
        ctx.writeAndFlush(packetSerializer.serialize(confirm));

        log.info("User {} joined room {}", username, chatId);
    }

    @Transactional
    public void leaveRoom(String username, String chatId) {
        chatParticipantRepository.findByChatIdAndUsername(chatId, username).ifPresent(p -> {
            p.setIsActive(false);
            p.setLeftAt(LocalDateTime.now());
            chatParticipantRepository.save(p);
        });

        List<String> others = getActiveParticipantUsernames(chatId);
        FusionPacket leaveNotif = new FusionPacket(PacketType.CHATROOM_USER_STATUS);
        leaveNotif.addField(FIELD_CHAT_ID, chatId);
        leaveNotif.addField(FIELD_USERNAME, username);
        leaveNotif.addField(FIELD_STATUS, (short) 0);
        connectionManager.broadcastPacket(others, leaveNotif);

        log.info("User {} left room {}", username, chatId);
    }

    public List<ChatParticipant> getParticipants(String chatId) {
        return chatParticipantRepository.findByChatIdAndIsActiveTrue(chatId);
    }

    public List<String> getActiveParticipantUsernames(String chatId) {
        return getParticipants(chatId).stream()
                .map(ChatParticipant::getUsername)
                .collect(Collectors.toList());
    }
}
