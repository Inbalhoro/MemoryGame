package com.horovitz.memorygame;

public enum Theme {
    CARTOON_CHARACTERS("image"),
    ANIMALS("animal"),
    FOOD("food"),
    FLAGS("flag");

    private final String prefix;

    Theme(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
