package com.eu.habbo.habbohotel.wired.highscores;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WiredHighscoreScoreType {
    PERTEAM(0),
    MOSTWIN(1),
    CLASSIC(2);

    private final int type;
}
