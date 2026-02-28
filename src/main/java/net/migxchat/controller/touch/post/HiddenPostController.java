package net.migxchat.controller.touch.post;

import net.migxchat.dto.request.CreatePostRequest;
import net.migxchat.dto.response.PostResponse;
import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostType;
import net.migxchat.service.post.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/touch/post")
public class HiddenPostController {

    private static final Logger log = LoggerFactory.getLogger(HiddenPostController.class);

    @Autowired
    private PostService postService;

    @Value("${app.upload.image-path:/var/uploads/images}")
    private String imagePath;

    @Value("${app.upload.video-path:/var/uploads/videos}")
    private String videoPath;

    @PostMapping(value = "/hidden_post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadHiddenPost(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam(value = "privacy", required = false, defaultValue = "1") Integer privacy,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Username", required = false) String username) {

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }

        CreatePostRequest request = new CreatePostRequest();
        request.setBody(body);
        request.setPrivacy(net.migxchat.model.post.PostPrivacy.fromValue(privacy));

        try {
            if (photo != null && !photo.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                Path uploadDir = Paths.get(imagePath);
                Files.createDirectories(uploadDir);
                Files.copy(photo.getInputStream(), uploadDir.resolve(filename));
                request.setPhotoUrl("/uploads/images/" + filename);
                request.setType(PostType.PHOTO);
            } else if (video != null && !video.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + video.getOriginalFilename();
                Path uploadDir = Paths.get(videoPath);
                Files.createDirectories(uploadDir);
                Files.copy(video.getInputStream(), uploadDir.resolve(filename));
                request.setVideoUrl("/uploads/videos/" + filename);
                request.setType(PostType.VIDEO);
            }
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file"));
        }

        Post post = postService.createPost(request, userId, username != null ? username : userId);
        return ResponseEntity.ok(PostResponse.from(post));
    }
}
