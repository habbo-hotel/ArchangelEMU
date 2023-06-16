package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import lombok.Getter;
import lombok.Setter;

public interface IWiredSettings {
    int[] getIntegerParams();
    void setIntegerParams(int[] value);
    String getStringParam();
    void setStringParam(String value);
    int[] getItems();
    void setItems(int[] value);
    int getDelay();
    void setDelay(int value);
    int getSelectionType();
    void setSelectionType(int value);
}
