package com.eu.habbo.habbohotel.items.interactions.totems;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TotemType {

    NONE(0),
    TROLL(1),
    SNAKE(2),
    BIRD(3);

    private final int type;



    public static TotemType fromInt(int type) {
        for(TotemType totemType : TotemType.values()) {
            if(totemType.type == type)
                return totemType;
        }

        return NONE;
    }
}
