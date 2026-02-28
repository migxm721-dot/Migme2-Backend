package net.migxchat.service.post;

import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostType;
import net.migxchat.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    private PostRepository postRepository;

    public Page<Post> getHomeFeeds(String userId, Pageable pageable, List<PostType> excludedTypes) {
        if (excludedTypes != null && !excludedTypes.isEmpty()) {
            return postRepository.findByAuthorIdExcludingTypes(userId, excludedTypes, pageable);
        }
        return postRepository.findByAuthorId(userId, pageable);
    }
}
