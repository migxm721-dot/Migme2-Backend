package net.migxchat.repository;

import net.migxchat.model.credit.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    Optional<Credit> findByUserId(String userId);
}
