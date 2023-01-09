package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolChatlogType {
    BOT_PET(0),
    YELLOW(1),
    BLUE(2);

    private  final int type;

}