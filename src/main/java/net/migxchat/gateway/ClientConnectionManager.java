package net.migxchat.gateway;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import net.migxchat.protocol.FusionPacket;
import net.migxchat.protocol.PacketSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ClientConnectionManager.class);

    private final Map<ChannelId, ConnectionSession> sessionsByChannel = new ConcurrentHashMap<>();
    private final Map<String, ConnectionSession> sessionsByUsername = new ConcurrentHashMap<>();

    @Autowired
    private PacketSerializer packetSerializer;

    public void registerConnection(Channel channel, String ipAddress) {
        ConnectionSession session = new ConnectionSession(channel, ipAddress);
        sessionsByChannel.put(channel.id(), session);
        log.info("New connection registered: {} from {}", channel.id(), ipAddress);
    }

    public void removeConnection(Channel channel) {
        ConnectionSession session = sessionsByChannel.remove(channel.id());
        if (session != null && session.getUsername() != null) {
            sessionsByUsername.remove(session.getUsername());
            log.info("Connection removed for user: {}", session.getUsername());
        }
    }

    public void authenticateSession(Channel channel, String username, String sessionId) {
        ConnectionSession session = sessionsByChannel.get(channel.id());
        if (session != null) {
            session.setUsername(username);
            session.setSessionId(sessionId);
            session.setAuthenticated(true);
            sessionsByUsername.put(username, session);
            log.info("Session authenticated for user: {}", username);
        }
    }

    public ConnectionSession getSessionByChannel(Channel channel) {
        return sessionsByChannel.get(channel.id());
    }

    public ConnectionSession getSessionByUsername(String username) {
        return sessionsByUsername.get(username);
    }

    public boolean isUserOnline(String username) {
        ConnectionSession session = sessionsByUsername.get(username);
        return session != null && session.isActive();
    }

    public void sendPacket(String username, FusionPacket packet) {
        ConnectionSession session = sessionsByUsername.get(username);
        if (session != null && session.isActive()) {
            session.getChannel().writeAndFlush(packetSerializer.serialize(packet));
        }
    }

    public void broadcastPacket(Collection<String> usernames, FusionPacket packet) {
        for (String username : usernames) {
            sendPacket(username, packet);
        }
    }

    public int getActiveConnectionCount() {
        return sessionsByChannel.size();
    }
}
