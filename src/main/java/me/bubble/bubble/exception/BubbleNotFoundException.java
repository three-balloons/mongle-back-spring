package me.bubble.bubble.exception;

public class BubbleNotFoundException extends RuntimeException{
    public BubbleNotFoundException(String message) {
        super(message);
    }
}
