package net.migxchat.gateway;

import io.netty.channel.Channel;
import lombok.Data;

import java.time.Instant;

@Data
public class ConnectionSession {
    private String sessionId;
    private String username;
    private Channel channel;
    private String ipAddress;
    private boolean authenticated;
    private Instant connectedAt;
    private Instant lastActivity;

    public ConnectionSession(Channel channel, String ipAddress) {
        this.channel = channel;
        this.ipAddress = ipAddress;
        this.authenticated = false;
        this.connectedAt = Instant.now();
        this.lastActivity = Instant.now();
    }

    public void updateLastActivity() {
        this.lastActivity = Instant.now();
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }
}
