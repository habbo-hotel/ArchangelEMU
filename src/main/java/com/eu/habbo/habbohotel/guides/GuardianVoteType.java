package com.eu.habbo.habbohotel.guides;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GuardianVoteType {
    FORWARDED(-1),
    WAITING(0),
    ACCEPTABLY(1),
    BADLY(2),
    AWFULLY(3),
    NOT_VOTED(4),
    SEARCHING(5);

    @Getter
    private final int type;

}
