package net.migxchat.services;

import io.netty.channel.ChannelHandlerContext;
import net.migxchat.gateway.ClientConnectionManager;
import net.migxchat.models.Message;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketSerializer;
import net.migxchat.protocol.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public static final short FIELD_MESSAGE_ID = 1;
    public static final short FIELD_SOURCE = 2;
    public static final short FIELD_DESTINATION = 3;
    public static final short FIELD_CONTENT_TYPE = 4;
    public static final short FIELD_BODY = 5;
    public static final short FIELD_TIMESTAMP = 6;
    public static final short FIELD_DEST_TYPE = 7;
    public static final short FIELD_CHAT_ID = 10;
    public static final short FIELD_STATUS = 20;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ClientConnectionManager connectionManager;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private PacketSerializer packetSerializer;

    public void handleMessage(ChannelHandlerContext ctx, FusionPacket packet) {
        var session = connectionManager.getSessionByChannel(ctx.channel());
        if (session == null || !session.isAuthenticated()) return;

        String destination = packet.getStringField(FIELD_DESTINATION);
        Short contentType = packet.getField(FIELD_CONTENT_TYPE, Short.class);
        String body = packet.getStringField(FIELD_BODY);
        Short destType = packet.getField(FIELD_DEST_TYPE, Short.class);

        if (destination == null || body == null) return;

        short dType = destType != null ? destType : 0;
        short cType = contentType != null ? contentType : 0;

        Message saved = messageService.saveMessage(session.getUsername(), destination, dType, cType, body);

        // Send to recipient if online
        FusionPacket msgPacket = new FusionPacket(PacketType.MESSAGE);
        msgPacket.setSequenceNumber(packet.getSequenceNumber());
        msgPacket.addField(FIELD_MESSAGE_ID, saved.getMessageId());
        msgPacket.addField(FIELD_SOURCE, session.getUsername());
        msgPacket.addField(FIELD_DESTINATION, destination);
        msgPacket.addField(FIELD_BODY, body);
        msgPacket.addField(FIELD_TIMESTAMP, saved.getTimestamp());
        msgPacket.addField(FIELD_CONTENT_TYPE, cType);

        if (connectionManager.isUserOnline(destination)) {
            connectionManager.sendPacket(destination, msgPacket);
        }

        // Send delivery acknowledgment to sender
        FusionPacket ack = new FusionPacket(PacketType.MESSAGE_STATUS_EVENT);
        ack.setSequenceNumber(packet.getSequenceNumber());
        ack.addField(FIELD_MESSAGE_ID, saved.getMessageId());
        ack.addField(FIELD_STATUS, (short) 1);
        ctx.writeAndFlush(packetSerializer.serialize(ack));

        log.debug("Message from {} to {} delivered", session.getUsername(), destination);
    }

    public void handleChat(ChannelHandlerContext ctx, FusionPacket packet) {
        handleMessage(ctx, packet);
    }

    public void handleJoinChatRoom(ChannelHandlerContext ctx, FusionPacket packet) {
        var session = connectionManager.getSessionByChannel(ctx.channel());
        if (session == null || !session.isAuthenticated()) return;

        String chatId = packet.getStringField(FIELD_CHAT_ID);
        if (chatId == null) return;

        chatRoomService.joinRoom(session.getUsername(), chatId, ctx, packet.getSequenceNumber());
    }

    public void handleLeaveChatRoom(ChannelHandlerContext ctx, FusionPacket packet) {
        var session = connectionManager.getSessionByChannel(ctx.channel());
        if (session == null || !session.isAuthenticated()) return;

        String chatId = packet.getStringField(FIELD_CHAT_ID);
        if (chatId == null) return;

        chatRoomService.leaveRoom(session.getUsername(), chatId);
    }
}
