package com.eu.habbo.habbohotel.wired.highscores;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WiredHighscoreClearType {
    ALLTIME(0),
    DAILY(1),
    WEEKLY(2),
    MONTHLY(3);

    private final int type;


}
