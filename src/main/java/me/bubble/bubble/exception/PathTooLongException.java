package me.bubble.bubble.exception;

public class PathTooLongException extends RuntimeException{
    public PathTooLongException(String message) {
        super(message);
    }
}
