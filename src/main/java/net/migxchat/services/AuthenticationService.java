package net.migxchat.services;

import io.netty.channel.ChannelHandlerContext;
import net.migxchat.gateway.ClientConnectionManager;
import net.migxchat.models.Session;
import net.migxchat.models.User;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketSerializer;
import net.migxchat.protocol.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Optional;

@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    // Field IDs for Fusion Protocol
    public static final short FIELD_USERNAME = 1;
    public static final short FIELD_PASSWORD = 2;
    public static final short FIELD_SESSION_ID = 3;
    public static final short FIELD_USER_ID = 4;
    public static final short FIELD_DISPLAY_NAME = 5;
    public static final short FIELD_STATUS = 6;
    public static final short FIELD_ERROR_CODE = 7;
    public static final short FIELD_ERROR_MSG = 8;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ClientConnectionManager connectionManager;

    @Autowired
    private PacketSerializer packetSerializer;

    public void handleLogin(ChannelHandlerContext ctx, FusionPacket packet) {
        String username = packet.getStringField(FIELD_USERNAME);
        String password = packet.getStringField(FIELD_PASSWORD);

        if (username == null || password == null) {
            sendError(ctx, packet.getSequenceNumber(), 400, "Username and password required");
            return;
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty() || !userService.verifyPassword(userOpt.get(), password)) {
            sendError(ctx, packet.getSequenceNumber(), 401, "Invalid credentials");
            return;
        }

        User user = userOpt.get();
        String ipAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        String channelId = ctx.channel().id().asLongText();

        Session session = sessionService.createSession(username, ipAddress, null, channelId);
        connectionManager.authenticateSession(ctx.channel(), username, session.getSessionId());
        userService.updateLastLogin(username);
        userService.updatePresence(username, (short) 1);

        FusionPacket response = new FusionPacket(PacketType.LOGIN_OK);
        response.setSequenceNumber(packet.getSequenceNumber());
        response.addField(FIELD_SESSION_ID, session.getSessionId());
        response.addField(FIELD_USER_ID, user.getUserId());
        response.addField(FIELD_USERNAME, user.getUsername());
        response.addField(FIELD_DISPLAY_NAME, user.getDisplayName() != null ? user.getDisplayName() : username);
        response.addField(FIELD_STATUS, (short) 1);
        ctx.writeAndFlush(packetSerializer.serialize(response));

        log.info("User {} logged in successfully", username);
    }

    public void handleLogout(ChannelHandlerContext ctx, FusionPacket packet) {
        var session = connectionManager.getSessionByChannel(ctx.channel());
        if (session != null && session.getSessionId() != null) {
            sessionService.invalidateSession(session.getSessionId());
            if (session.getUsername() != null) {
                userService.updatePresence(session.getUsername(), (short) 0);
            }
        }
        ctx.close();
    }

    private void sendError(ChannelHandlerContext ctx, int seqNum, int code, String message) {
        FusionPacket error = new FusionPacket(PacketType.ERROR);
        error.setSequenceNumber(seqNum);
        error.addField(FIELD_ERROR_CODE, code);
        error.addField(FIELD_ERROR_MSG, message);
        ctx.writeAndFlush(packetSerializer.serialize(error));
    }
}
