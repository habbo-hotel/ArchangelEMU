package com.eu.habbo.habbohotel.rooms.entities.items;

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