package com.example.usermanagement.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("User with id " + id + " was not found");
    }
}

