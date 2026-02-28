package net.migxchat.repository;

import net.migxchat.model.post.Post;
import net.migxchat.model.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.isDeleted = false " +
           "AND p.type NOT IN :excludedTypes ORDER BY p.timestamp DESC")
    Page<Post> findByAuthorIdExcludingTypes(@Param("authorId") String authorId,
                                             @Param("excludedTypes") List<PostType> excludedTypes,
                                             Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.isDeleted = false ORDER BY p.timestamp DESC")
    Page<Post> findByAuthorId(@Param("authorId") String authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.parentPostId = :postId AND p.isDeleted = false ORDER BY p.timestamp ASC")
    Page<Post> findRepliesByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.rootPostId = :postId AND p.parentPostId = :postId " +
           "AND p.isDeleted = false ORDER BY p.timestamp DESC")
    Page<Post> findResharesByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.privacy = net.migxchat.model.post.PostPrivacy.PUBLIC " +
           "AND LOWER(p.body) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.timestamp DESC")
    Page<Post> searchByBody(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.privacy = net.migxchat.model.post.PostPrivacy.PUBLIC " +
           "AND LOWER(p.tags) LIKE LOWER(CONCAT('%', :topic, '%')) ORDER BY p.timestamp DESC")
    Page<Post> findByTopic(@Param("topic") String topic, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.groupId = :groupId AND p.isDeleted = false ORDER BY p.timestamp DESC")
    Page<Post> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
}
