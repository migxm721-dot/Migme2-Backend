package net.migxchat.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketParser;
import net.migxchat.protocol.PacketSerializer;
import net.migxchat.protocol.PacketType;
import net.migxchat.services.AuthenticationService;
import net.migxchat.services.ChatService;
import net.migxchat.services.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    @Autowired
    private PacketParser packetParser;

    @Autowired
    private PacketSerializer packetSerializer;

    @Autowired
    private ClientConnectionManager connectionManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private PresenceService presenceService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String ipAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        connectionManager.registerConnection(ctx.channel(), ipAddress);
        log.debug("Channel active: {}", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ConnectionSession session = connectionManager.getSessionByChannel(ctx.channel());
        if (session != null && session.getUsername() != null) {
            presenceService.setOffline(session.getUsername());
        }
        connectionManager.removeConnection(ctx.channel());
        log.debug("Channel inactive: {}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof ByteBuf buf)) {
            return;
        }
        try {
            FusionPacket packet = packetParser.parse(buf);
            if (packet == null) return;
            routePacket(ctx, packet);
        } finally {
            buf.release();
        }
    }

    private void routePacket(ChannelHandlerContext ctx, FusionPacket packet) {
        ConnectionSession session = connectionManager.getSessionByChannel(ctx.channel());
        if (session != null) session.updateLastActivity();

        switch (packet.getType()) {
            case LOGIN -> authenticationService.handleLogin(ctx, packet);
            case LOGOUT -> authenticationService.handleLogout(ctx, packet);
            case MESSAGE -> chatService.handleMessage(ctx, packet);
            case CHAT -> chatService.handleChat(ctx, packet);
            case JOIN_CHATROOM -> chatService.handleJoinChatRoom(ctx, packet);
            case LEAVE_CHATROOM -> chatService.handleLeaveChatRoom(ctx, packet);
            case PRESENCE -> presenceService.handlePresenceUpdate(ctx, packet);
            case PING -> handlePing(ctx, packet);
            default -> log.debug("Unhandled packet type: {}", packet.getType());
        }
    }

    private void handlePing(ChannelHandlerContext ctx, FusionPacket ping) {
        FusionPacket pong = new FusionPacket(PacketType.PONG);
        pong.setSequenceNumber(ping.getSequenceNumber());
        ctx.writeAndFlush(packetSerializer.serialize(pong));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in channel {}: {}", ctx.channel().id(), cause.getMessage(), cause);
        ctx.close();
    }
}
