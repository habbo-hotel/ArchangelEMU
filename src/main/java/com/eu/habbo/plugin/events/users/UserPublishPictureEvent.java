package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserPublishPictureEvent extends UserEvent {
    private final String URL;

    private final int timestamp;

    private final int roomId;


    public UserPublishPictureEvent(Habbo habbo, String url, int timestamp, int roomId) {
        super(habbo);
        this.URL = url;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
}