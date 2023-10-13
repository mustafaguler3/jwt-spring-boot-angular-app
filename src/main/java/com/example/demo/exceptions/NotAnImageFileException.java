package com.example.demo.exceptions;

public class NotAnImageFileException extends RuntimeException{
    public NotAnImageFileException(String message) {
        super(message);
    }
}
