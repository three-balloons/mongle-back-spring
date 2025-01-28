package me.bubble.bubble.exception;

public class FileUploadFailedException extends RuntimeException{
    public FileUploadFailedException(String message) {
        super(message);
    }
}