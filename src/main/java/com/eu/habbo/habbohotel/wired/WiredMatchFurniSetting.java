package com.eu.habbo.habbohotel.wired;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WiredMatchFurniSetting {
    private int item_id;
    private String state;
    private int rotation;
    private int x;
    private int y;

    public WiredMatchFurniSetting(int item_id, String state, int rotation, int x, int y) {
        this.item_id = item_id;
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
