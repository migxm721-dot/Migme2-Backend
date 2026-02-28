package net.migxchat.repository;

import net.migxchat.model.post.PostMention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMentionRepository extends JpaRepository<PostMention, Long> {

    @Query("SELECT pm FROM PostMention pm WHERE pm.mentionedUserId = :userId ORDER BY pm.createdAt DESC")
    Page<PostMention> findByMentionedUserId(@Param("userId") String userId, Pageable pageable);
}
