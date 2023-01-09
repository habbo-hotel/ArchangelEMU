package com.eu.habbo.habbohotel.modtool;

import lombok.Getter;

@Getter
public class ModToolSanctionItem {
    private  int id;
    private  int habboId;
    private  int sanctionLevel;
    private  int probationTimestamp;
    private  boolean isMuted;
    private  int muteDuration;
    private  int tradeLockedUntil;
    private  String reason;

    public ModToolSanctionItem(int id, int habboId, int sanctionLevel, int probationTimestamp, boolean isMuted, int muteDuration, int tradeLockedUntil, String reason) {
        this.id = id;
        this.habboId = habboId;
        this.sanctionLevel = sanctionLevel;
        this.probationTimestamp = probationTimestamp;
        this.isMuted = isMuted;
        this.muteDuration = muteDuration;
        this.tradeLockedUntil = tradeLockedUntil;
        this.reason = reason;
    }


}
