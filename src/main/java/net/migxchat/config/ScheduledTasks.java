package net.migxchat.config;

import net.migxchat.service.chatroom.ChatroomModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ChatroomModerationService moderationService;

    @Scheduled(fixedRateString = "${app.moderation.cleanup-interval:3600000}")
    public void cleanupExpiredModeration() {
        moderationService.cleanupExpiredModeration();
    }

    @Scheduled(fixedRate = 300000)
    public void logSystemHealth() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        log.debug("System health - Memory used: {}MB / {}MB", usedMemory, totalMemory);
    }
}
