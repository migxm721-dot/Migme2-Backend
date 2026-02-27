package net.migxchat.repositories;

import net.migxchat.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(String messageId);
    List<Message> findByDestinationAndTimestampLessThanOrderByTimestampDesc(String destination, Long timestamp, Pageable pageable);
    List<Message> findByDestinationOrderByTimestampDesc(String destination, Pageable pageable);
}
