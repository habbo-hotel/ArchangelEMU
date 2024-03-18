package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserPurchasePictureEvent extends UserEvent {
    private final String url;
    private final int roomId;
    private final int timestamp;


    public UserPurchasePictureEvent(Habbo habbo, String url, int roomId, int timestamp) {
        super(habbo);

        this.url = url;
        this.roomId = roomId;
        this.timestamp = timestamp;
    }
}