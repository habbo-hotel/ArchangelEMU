package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class WiredChangeDirectionSetting {
    private final int item_id;
    private int rotation;
    @Setter private RoomUserRotation direction;

}
