package com.eu.habbo.habbohotel.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DanceType {
    NONE(0),
    HAB_HOP(1),
    POGO_MOGO(2),
    DUCK_FUNK(3),
    THE_ROLLIE(4);

    private final int type;
}
