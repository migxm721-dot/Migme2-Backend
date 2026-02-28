package net.migxchat.repository;

import net.migxchat.model.post.PostWatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostWatchRepository extends JpaRepository<PostWatch, Long> {

    Optional<PostWatch> findByUserIdAndPostId(String userId, Long postId);

    boolean existsByUserIdAndPostId(String userId, Long postId);

    @Query("SELECT pw FROM PostWatch pw WHERE pw.userId = :userId ORDER BY pw.watchedAt DESC")
    Page<PostWatch> findByUserId(@Param("userId") String userId, Pageable pageable);
}
