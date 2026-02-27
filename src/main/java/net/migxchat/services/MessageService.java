package net.migxchat.services;

import net.migxchat.models.Message;
import net.migxchat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public Message saveMessage(String sourceUsername, String destination, short destinationType,
                                short contentType, String body) {
        Message msg = new Message();
        msg.setMessageId(UUID.randomUUID().toString());
        msg.setMessageType((short) 0);
        msg.setSourceUsername(sourceUsername);
        msg.setDestination(destination);
        msg.setDestinationType(destinationType);
        msg.setContentType(contentType);
        msg.setMessageBody(body);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setDeliveryStatus((short) 0);
        return messageRepository.save(msg);
    }

    public List<Message> getMessageHistory(String destination, int limit, long beforeTimestamp) {
        if (beforeTimestamp > 0) {
            return messageRepository.findByDestinationAndTimestampLessThanOrderByTimestampDesc(
                    destination, beforeTimestamp, PageRequest.of(0, limit));
        }
        return messageRepository.findByDestinationOrderByTimestampDesc(destination, PageRequest.of(0, limit));
    }

    @Transactional
    public void updateDeliveryStatus(String messageId, short status) {
        messageRepository.findByMessageId(messageId).ifPresent(msg -> {
            msg.setDeliveryStatus(status);
            messageRepository.save(msg);
        });
    }

    public Optional<Message> findByMessageId(String messageId) {
        return messageRepository.findByMessageId(messageId);
    }
}
