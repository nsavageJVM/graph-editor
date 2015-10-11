package com.kikul.model.editor.dragicon;

public enum DragIconType {

    NODE("Simple node"),
    JOINT("Joint node"),
    GLOBAL("Global node");

    private final String value;

    DragIconType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}