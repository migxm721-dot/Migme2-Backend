package net.migxchat.services;

import net.migxchat.models.Contact;
import net.migxchat.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> getContacts(String username) {
        return contactRepository.findByUserUsername(username);
    }

    @Transactional
    public Contact addContact(String userUsername, String contactUsername, String displayName) {
        if (contactRepository.existsByUserUsernameAndContactUsername(userUsername, contactUsername)) {
            return contactRepository.findByUserUsernameAndContactUsername(userUsername, contactUsername)
                    .orElseGet(() -> {
                        Contact c = new Contact();
                        c.setUserUsername(userUsername);
                        c.setContactUsername(contactUsername);
                        c.setDisplayName(displayName);
                        return contactRepository.save(c);
                    });
        }
        Contact contact = new Contact();
        contact.setUserUsername(userUsername);
        contact.setContactUsername(contactUsername);
        contact.setDisplayName(displayName);
        return contactRepository.save(contact);
    }

    @Transactional
    public void removeContact(String userUsername, String contactUsername) {
        contactRepository.findByUserUsernameAndContactUsername(userUsername, contactUsername)
                .ifPresent(contactRepository::delete);
    }

    public Optional<Contact> findContact(String userUsername, String contactUsername) {
        return contactRepository.findByUserUsernameAndContactUsername(userUsername, contactUsername);
    }
}
