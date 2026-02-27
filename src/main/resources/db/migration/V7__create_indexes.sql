CREATE INDEX idx_messages_source_dest ON messages(source_username, destination);
CREATE INDEX idx_contacts_user ON contacts(user_username);
CREATE INDEX idx_contacts_contact ON contacts(contact_username);
