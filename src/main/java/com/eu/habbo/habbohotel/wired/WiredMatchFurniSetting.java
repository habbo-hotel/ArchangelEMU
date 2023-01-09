package com.eu.habbo.habbohotel.wired;

import lombok.Getter;

@Getter
public class WiredMatchFurniSetting {
    private final int item_id;
    private final String state;
    private final int rotation;
    private final int x;
    private final int y;

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y) {
        this.item_id = itemId;
        this.state = state.replace("\t\t\t", " ");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean includeState) {
        return this.item_id + "-" + (this.state.isEmpty() || !includeState ? " " : this.state) + "-" + this.rotation + "-" + this.x + "-" + this.y;
    }

}
