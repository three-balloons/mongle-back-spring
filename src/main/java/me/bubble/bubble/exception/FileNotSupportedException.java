package me.bubble.bubble.exception;

public class FileNotSupportedException extends RuntimeException{
    public FileNotSupportedException(String message) {
        super(message);
    }
}