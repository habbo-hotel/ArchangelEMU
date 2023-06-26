package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;

import java.util.List;

public interface InteractionWiredMatchFurniSettings {
    List<WiredMatchFurniSetting> getMatchSettings();
    boolean shouldMatchState();
    boolean shouldMatchRotation();
    boolean shouldMatchPosition();
}
