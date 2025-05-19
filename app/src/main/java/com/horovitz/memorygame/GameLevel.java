package com.horovitz.memorygame;

public enum GameLevel {
    EASY(6),
    REGULAR(16),
    HARD(36);

    private final int buttonCount;

    GameLevel(int buttonCount) {
        this.buttonCount = buttonCount;
    }

    public int getButtonCount() {
        return buttonCount;
    }
}

