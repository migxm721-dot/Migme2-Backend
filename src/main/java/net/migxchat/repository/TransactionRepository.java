package net.migxchat.repository;

import net.migxchat.model.credit.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromUserId = :userId OR t.toUserId = :userId ORDER BY t.timestamp DESC")
    Page<Transaction> findByUserId(@Param("userId") String userId, Pageable pageable);
}
