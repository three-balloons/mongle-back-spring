package me.bubble.bubble.exception;

public class InappropriateUserException extends RuntimeException{
    public InappropriateUserException(String message) {
        super(message);
    }
}
