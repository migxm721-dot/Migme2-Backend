package net.migxchat.controller.touch.datasvc;

import net.migxchat.dto.request.CreatePostRequest;
import net.migxchat.dto.response.PostResponse;
import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostWatch;
import net.migxchat.service.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/touch/datasvc")
public class PostDataServiceController {

    @Autowired
    private PostService postService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private MentionService mentionService;

    @Autowired
    private WatchlistService watchlistService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId,
                                      @RequestHeader(value = "X-User-Id", required = false) String userId) {
        Post post = postService.getPostById(postId, userId);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<?> getUserPosts(@PathVariable String userId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Page<Post> posts = postService.getUserPosts(userId, PageRequest.of(page, size), null);
        List<PostResponse> responses = posts.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("posts", responses, "total", posts.getTotalElements(), "page", page));
    }

    @GetMapping("/feed/home")
    public ResponseEntity<?> getHomeFeed(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Page<Post> feed = feedService.getHomeFeeds(userId, PageRequest.of(page, size), null);
        List<PostResponse> responses = feed.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("posts", responses, "total", feed.getTotalElements(), "page", page));
    }

    @GetMapping("/mentions")
    public ResponseEntity<?> getMentions(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        var mentions = mentionService.getMentionsForUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of("mentions", mentions.getContent(), "total", mentions.getTotalElements()));
    }

    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchlist(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Page<PostWatch> watchlist = watchlistService.getWatchedPosts(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of("watchlist", watchlist.getContent(), "total", watchlist.getTotalElements()));
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request,
                                         @RequestHeader(value = "X-User-Id", required = false) String userId,
                                         @RequestHeader(value = "X-Username", required = false) String username) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Post post = postService.createPost(request, userId, username != null ? username : userId);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                         @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }

    @PostMapping("/post/{postId}/watch")
    public ResponseEntity<?> watchPost(@PathVariable Long postId,
                                        @RequestHeader(value = "X-User-Id", required = false) String userId,
                                        @RequestParam(defaultValue = "true") boolean watch) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        if (watch) {
            PostWatch postWatch = watchlistService.watchPost(postId, userId);
            return ResponseEntity.ok(Map.of("message", "Post watched", "watchId", postWatch.getId()));
        } else {
            watchlistService.unwatchPost(postId, userId);
            return ResponseEntity.ok(Map.of("message", "Post unwatched"));
        }
    }

    @PostMapping("/post/{postId}/lock")
    public ResponseEntity<?> lockPost(@PathVariable Long postId,
                                       @RequestHeader(value = "X-User-Id", required = false) String userId,
                                       @RequestParam(defaultValue = "true") boolean lock) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Post post = postService.lockPost(postId, userId, lock);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    @PostMapping("/post/{postId}/tag")
    public ResponseEntity<?> tagPost(@PathVariable Long postId,
                                      @RequestHeader(value = "X-User-Id", required = false) String userId,
                                      @RequestBody Map<String, Integer> body) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Post post = postService.tagPost(postId, userId, body.get("tagId"));
        return ResponseEntity.ok(PostResponse.from(post));
    }

    @GetMapping("/post/{postId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable Long postId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        Page<Post> replies = postService.getRepliesForPost(postId, PageRequest.of(page, size));
        List<PostResponse> responses = replies.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("replies", responses, "total", replies.getTotalElements()));
    }

    @GetMapping("/post/{postId}/reshares")
    public ResponseEntity<?> getReshares(@PathVariable Long postId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Page<Post> reshares = postService.getResharesForPost(postId, PageRequest.of(page, size));
        List<PostResponse> responses = reshares.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("reshares", responses, "total", reshares.getTotalElements()));
    }

    @GetMapping("/search/posts")
    public ResponseEntity<?> searchPosts(@RequestParam String q,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Page<Post> results = postService.searchPosts(q, PageRequest.of(page, size));
        List<PostResponse> responses = results.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("posts", responses, "total", results.getTotalElements()));
    }

    @GetMapping("/hottopic/{topic}/posts")
    public ResponseEntity<?> getTopicPosts(@PathVariable String topic,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        Page<Post> results = postService.getPostsByTopic(topic, PageRequest.of(page, size));
        List<PostResponse> responses = results.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("posts", responses, "total", results.getTotalElements(), "topic", topic));
    }

    @GetMapping("/group/{groupId}/posts")
    public ResponseEntity<?> getGroupPosts(@PathVariable Long groupId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        Page<Post> results = postService.getGroupPosts(groupId, PageRequest.of(page, size));
        List<PostResponse> responses = results.getContent().stream().map(PostResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("posts", responses, "total", results.getTotalElements()));
    }
}
