package com.example.usermanagement.exception;

public class DuplicatedEmailException extends RuntimeException {

    public DuplicatedEmailException() {
        super("Email is already registered");
    }
}

