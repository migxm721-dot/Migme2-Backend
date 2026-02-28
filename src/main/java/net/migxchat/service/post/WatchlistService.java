package net.migxchat.service.post;

import net.migxchat.exception.PostNotFoundException;
import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostWatch;
import net.migxchat.repository.PostRepository;
import net.migxchat.repository.PostWatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WatchlistService {

    @Autowired
    private PostWatchRepository watchRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public PostWatch watchPost(Long postId, String userId) {
        if (watchRepository.existsByUserIdAndPostId(userId, postId)) {
            return watchRepository.findByUserIdAndPostId(userId, postId).orElseThrow();
        }
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        post.setWatchCount((post.getWatchCount() == null ? 0 : post.getWatchCount()) + 1);
        postRepository.save(post);

        PostWatch watch = new PostWatch();
        watch.setUserId(userId);
        watch.setPostId(postId);
        return watchRepository.save(watch);
    }

    @Transactional
    public void unwatchPost(Long postId, String userId) {
        watchRepository.findByUserIdAndPostId(userId, postId).ifPresent(w -> {
            watchRepository.delete(w);
            postRepository.findById(postId).ifPresent(post -> {
                int count = post.getWatchCount() == null ? 0 : post.getWatchCount();
                post.setWatchCount(Math.max(0, count - 1));
                postRepository.save(post);
            });
        });
    }

    public Page<PostWatch> getWatchedPosts(String userId, Pageable pageable) {
        return watchRepository.findByUserId(userId, pageable);
    }
}
