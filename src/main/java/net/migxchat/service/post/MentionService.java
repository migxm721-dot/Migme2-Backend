package net.migxchat.service.post;

import net.migxchat.model.post.PostMention;
import net.migxchat.repository.PostMentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MentionService {

    @Autowired
    private PostMentionRepository mentionRepository;

    public Page<PostMention> getMentionsForUser(String userId, Pageable pageable) {
        return mentionRepository.findByMentionedUserId(userId, pageable);
    }
}
