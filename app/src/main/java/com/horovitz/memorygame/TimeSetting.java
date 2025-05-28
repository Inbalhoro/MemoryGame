package com.horovitz.memorygame;

public enum TimeSetting {
    SHORT(300),
    REGULAR(700),
    LONG(1200);

    private final int seconds;

    TimeSetting(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}

