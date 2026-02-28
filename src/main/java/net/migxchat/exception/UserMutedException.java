package net.migxchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserMutedException extends RuntimeException {
    public UserMutedException(String message) {
        super(message);
    }
}
