package com.eu.habbo.habbohotel.items.interactions.wired;

import lombok.Getter;
import lombok.Setter;

public class WiredSettings {
    @Getter
    @Setter
    private int[] integerParams;

    @Getter
    @Setter
    private String stringParam;

    @Getter
    @Setter
    private int[] items;
    @Getter
    @Setter
    private int delay;

    @Getter
    @Setter
    private int selectionType;

    public WiredSettings() {
        this.items = new int[0];
        this.integerParams = new int[0];
        this.stringParam = "";
        this.delay = 0;
        this.selectionType = 0;
    }
}
