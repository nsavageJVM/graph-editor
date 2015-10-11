package com.kikul.controller.resources;


public class GraphEditorException extends RuntimeException {

    private String message;

    public GraphEditorException() {
        super();
    }

    public GraphEditorException(String message) {
        super(message);
        this.message = message;
    }

    public GraphEditorException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public GraphEditorException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
