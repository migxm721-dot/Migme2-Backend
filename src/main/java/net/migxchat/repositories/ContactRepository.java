package net.migxchat.repositories;

import net.migxchat.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserUsername(String userUsername);
    Optional<Contact> findByUserUsernameAndContactUsername(String userUsername, String contactUsername);
    boolean existsByUserUsernameAndContactUsername(String userUsername, String contactUsername);
}
