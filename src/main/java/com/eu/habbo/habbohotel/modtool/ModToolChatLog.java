package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ModToolChatLog implements Comparable<ModToolChatLog> {
    private final int timestamp;
    private final int habboId;
    private final String username;
    private final String message;
    private boolean highlighted = false;


    @Override
    public int compareTo(ModToolChatLog o) {
        return o.timestamp - this.timestamp;
    }
}
