package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolTicketState {
    CLOSED(0),
    OPEN(1),
    PICKED(2);

    private final int state;


    public static ModToolTicketState getState(int number) {
        for (ModToolTicketState s : ModToolTicketState.values()) {
            if (s.state == number)
                return s;
        }

        return CLOSED;
    }

}
