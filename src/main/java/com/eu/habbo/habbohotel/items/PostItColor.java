package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostItColor {
    BLUE("9CCEFF"),
    GREEN("9CFF9C"),
    PINK("FF9CFF"),
    YELLOW("FFFF33");

    public final String hexColor;


    public static boolean isCustomColor(String color) {
        for (PostItColor postItColor : PostItColor.values()) {
            if (postItColor.hexColor.equalsIgnoreCase(color))
                return false;
        }

        return true;
    }

    public static PostItColor randomColorNotYellow() {
        return PostItColor.values()[Emulator.getRandom().nextInt(3)];
    }
}
