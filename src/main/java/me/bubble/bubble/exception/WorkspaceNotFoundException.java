package me.bubble.bubble.exception;

public class WorkspaceNotFoundException extends RuntimeException{
    public WorkspaceNotFoundException(String message) {
        super(message);
    }
}
