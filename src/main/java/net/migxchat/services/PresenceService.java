package net.migxchat.services;

import io.netty.channel.ChannelHandlerContext;
import net.migxchat.gateway.ClientConnectionManager;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketSerializer;
import net.migxchat.protocol.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {
    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);

    public static final short FIELD_USERNAME = 1;
    public static final short FIELD_PRESENCE = 2;
    public static final short FIELD_STATUS_MESSAGE = 3;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientConnectionManager connectionManager;

    @Autowired
    private PacketSerializer packetSerializer;

    public void setOffline(String username) {
        try {
            userService.updatePresence(username, (short) 0);
            log.info("User {} set to offline", username);
        } catch (Exception e) {
            log.warn("Failed to set user {} offline: {}", username, e.getMessage());
        }
    }

    public void handlePresenceUpdate(ChannelHandlerContext ctx, FusionPacket packet) {
        var session = connectionManager.getSessionByChannel(ctx.channel());
        if (session == null || !session.isAuthenticated()) return;

        Short presence = packet.getField(FIELD_PRESENCE, Short.class);
        if (presence != null) {
            userService.updatePresence(session.getUsername(), presence);
        }
    }
}
