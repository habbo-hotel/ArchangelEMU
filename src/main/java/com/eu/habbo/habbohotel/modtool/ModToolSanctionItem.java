package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModToolSanctionItem {
    private final int id;
    private final int habboId;
    private final int sanctionLevel;
    private final int probationTimestamp;
    private final boolean isMuted;
    private final int muteDuration;
    private final int tradeLockedUntil;
    private final String reason;
}
