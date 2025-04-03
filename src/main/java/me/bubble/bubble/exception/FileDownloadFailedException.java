package me.bubble.bubble.exception;

public class FileDownloadFailedException extends RuntimeException{
    public FileDownloadFailedException(String message) {
        super(message);
    }
}