package com.eu.habbo.habbohotel.items.interactions.totems;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TotemPlanetType {
    MOON(0),
    SUN(1),
    EARTH(2);

    private final int type;


    public static TotemPlanetType fromInt(int type) {
        for(TotemPlanetType planetType : TotemPlanetType.values()) {
            if(planetType.type == type)
                return planetType;
        }

        return MOON;
    }
}
