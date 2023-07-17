package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import lombok.Getter;

@Getter

public class FurnitureRoomTonerEvent extends FurnitureUserEvent {
    private final int hue;
    private final int saturation;
    private final int brightness;


    public FurnitureRoomTonerEvent(RoomItem furniture, Habbo habbo, int hue, int saturation, int brightness) {
        super(furniture, habbo);

        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }
}
