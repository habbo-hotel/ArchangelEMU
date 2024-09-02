package com.eu.habbo.roleplay.messages.incoming.controls;

import lombok.Getter;

@Getter
public enum MovementDirection {
    UP("up"),
    LEFT("left"),
    DOWN("down"),
    RIGHT("right"),
    STOP("stop");

    private final String key;

    MovementDirection(String key) {
        this.key = key;
    }

    public static MovementDirection fromKey(String key) {
        for (MovementDirection direction : MovementDirection.values()) {
            if (direction.getKey().equalsIgnoreCase(key)) {
                return direction;
            }
        }
        return null;
    }
}