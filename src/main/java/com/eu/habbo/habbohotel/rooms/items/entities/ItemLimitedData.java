package com.eu.habbo.habbohotel.rooms.items.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ItemLimitedData {
    private int stack;
    private int sells;
}