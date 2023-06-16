package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class WiredExclusionStrategy implements ExclusionStrategy {
    private WiredSettings wiredSettings;
    public WiredExclusionStrategy(WiredSettings settings) {
        this.wiredSettings = settings;
    }
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String fieldName = fieldAttributes.getName();

        switch (fieldName) {
            case "stringParam":
                return this.wiredSettings.getStringParam().isEmpty();
            case "integerParams":
                return this.wiredSettings.getIntegerParams().length == 0;
            case "delay":
                return this.wiredSettings.getDelay() == 0;
            case "items":
                return this.wiredSettings.getItems().length == 0;
            case "selectionType":
            default:
                return true;
        }
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
