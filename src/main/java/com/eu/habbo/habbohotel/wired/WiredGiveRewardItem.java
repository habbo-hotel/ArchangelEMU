package com.eu.habbo.habbohotel.wired;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WiredGiveRewardItem {
    private final int id;
    private final boolean badge;
    private final String data;
    private final int probability;


    public WiredGiveRewardItem(String dataString) {
        String[] data = dataString.split(",");

        this.id = Integer.parseInt(data[0]);
        this.badge = data[1].equalsIgnoreCase("0");
        this.data = data[2];
        this.probability = Integer.parseInt(data[3]);
    }

    @Override
    public String toString() {
        return this.id + "," + (this.badge ? 0 : 1) + "," + this.data + "," + this.probability;
    }

    public String wiredString() {
        return (this.badge ? 0 : 1) + "," + this.data + "," + this.probability;
    }
}
