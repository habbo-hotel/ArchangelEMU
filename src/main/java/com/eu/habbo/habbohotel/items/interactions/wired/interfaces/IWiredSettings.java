package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public interface IWiredSettings {
    List<Integer> getIntegerParams();
    void setIntegerParams(List<Integer> value);
    String getStringParam();
    void setStringParam(String value);
    List<Integer> getItemIds();
    void setItemIds(List<Integer> value);
    int getDelay();
    void setDelay(int value);
    int getSelectionType();
    void setSelectionType(int value);
}
