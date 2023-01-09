package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class RoomMoodlightData {
    @Getter
    @Setter
    private int id;
    @Getter
    private boolean enabled;
    @Getter
    @Setter
    private boolean backgroundOnly;
    @Setter
    @Getter
    private String color;
    @Setter
    @Getter
    private int intensity;

    public static RoomMoodlightData fromString(String s) {
        String[] data = s.split(",");

        if (data.length == 5) {
            return new RoomMoodlightData(Integer.parseInt(data[1]), data[0].equalsIgnoreCase("2"), data[2].equalsIgnoreCase("2"), data[3], Integer.parseInt(data[4]));
        } else {
            return new RoomMoodlightData(1, true, true, "#000000", 255);
        }
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public String toString() {
        return (this.enabled ? 2 : 1) + "," + this.id + "," + (this.backgroundOnly ? 2 : 1) + "," + this.color + "," + this.intensity;
    }
}
