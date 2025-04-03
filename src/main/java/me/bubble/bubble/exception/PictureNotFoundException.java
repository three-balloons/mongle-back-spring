package me.bubble.bubble.exception;

public class PictureNotFoundException extends RuntimeException{
    public PictureNotFoundException(String message) {
        super(message);
    }
}
