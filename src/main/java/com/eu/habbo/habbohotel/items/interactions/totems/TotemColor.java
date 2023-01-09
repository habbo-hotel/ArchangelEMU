package com.eu.habbo.habbohotel.items.interactions.totems;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TotemColor {

    NONE(0),
    RED(1),
    YELLOW(2),
    BLUE(3);

    private final int color;


    public static TotemColor fromInt(int color) {
        for(TotemColor totemColor : TotemColor.values()) {
            if(totemColor.color == color)
                return totemColor;
        }

        return NONE;
    }
}
