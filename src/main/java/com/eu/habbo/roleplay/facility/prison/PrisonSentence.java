package com.eu.habbo.roleplay.facility.prison;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class PrisonSentence {
    @Getter
    private final Habbo habbo;
    @Getter
    private final String crime;
    @Setter
    @Getter
    private final int timeLeft;
    @Setter
    @Getter
    private final int timeServed;
}
