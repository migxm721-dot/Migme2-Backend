package net.migxchat.service.post;

import net.migxchat.dto.request.CreatePostRequest;
import net.migxchat.exception.PostNotFoundException;
import net.migxchat.exception.UnauthorizedActionException;
import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostType;
import net.migxchat.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public Post createPost(CreatePostRequest request, String authorId, String authorUsername) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setAuthorUsername(authorUsername);
        post.setBody(request.getBody());
        post.setType(request.getType() != null ? request.getType() : PostType.TEXT);
        post.setPrivacy(request.getPrivacy() != null ? request.getPrivacy() : net.migxchat.model.post.PostPrivacy.PUBLIC);
        post.setPhotoUrl(request.getPhotoUrl());
        post.setVideoUrl(request.getVideoUrl());
        post.setLinkUrl(request.getLinkUrl());
        post.setLocation(request.getLocation());
        post.setTags(request.getTags());
        post.setParentPostId(request.getParentPostId());
        post.setRootPostId(request.getRootPostId());
        post.setGroupId(request.getGroupId());
        post.setIsDeleted(false);
        post.setIsLocked(false);
        return postRepository.save(post);
    }

    public Post getPostById(Long postId, String requestingUserId) {
        return postRepository.findById(postId)
            .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
            .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
    }

    public Page<Post> getUserPosts(String userId, Pageable pageable, List<PostType> excludedTypes) {
        if (excludedTypes != null && !excludedTypes.isEmpty()) {
            return postRepository.findByAuthorIdExcludingTypes(userId, excludedTypes, pageable);
        }
        return postRepository.findByAuthorId(userId, pageable);
    }

    @Transactional
    public void deletePost(Long postId, String userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        if (!post.getAuthorId().equals(userId)) {
            throw new UnauthorizedActionException("You can only delete your own posts");
        }
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    public Post lockPost(Long postId, String userId, boolean lock) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        if (!post.getAuthorId().equals(userId)) {
            throw new UnauthorizedActionException("You can only lock your own posts");
        }
        post.setIsLocked(lock);
        return postRepository.save(post);
    }

    @Transactional
    public Post tagPost(Long postId, String userId, Integer tagId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        post.setTagId(tagId);
        return postRepository.save(post);
    }

    public Page<Post> getRepliesForPost(Long postId, Pageable pageable) {
        return postRepository.findRepliesByPostId(postId, pageable);
    }

    public Page<Post> getResharesForPost(Long postId, Pageable pageable) {
        return postRepository.findResharesByPostId(postId, pageable);
    }

    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.searchByBody(query, pageable);
    }

    public Page<Post> getPostsByTopic(String topic, Pageable pageable) {
        return postRepository.findByTopic(topic, pageable);
    }

    public Page<Post> getGroupPosts(Long groupId, Pageable pageable) {
        return postRepository.findByGroupId(groupId, pageable);
    }
}
