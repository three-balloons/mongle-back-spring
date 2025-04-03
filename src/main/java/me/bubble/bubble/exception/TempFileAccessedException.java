package me.bubble.bubble.exception;

public class TempFileAccessedException extends RuntimeException{
    public TempFileAccessedException(String message) {
        super(message);
    }
}
