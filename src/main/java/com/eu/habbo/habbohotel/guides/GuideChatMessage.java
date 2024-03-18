package com.eu.habbo.habbohotel.guides;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideChatMessage {
    private final int userId;
    private final String message;
    private final int timestamp;
}
