package com.eu.habbo.roleplay.messages.outgoing.game;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.game.TimeOfDayManager;
import lombok.AllArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
public class TimeOfDayComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.timeOfDayComposer);
        Date currentTime = new Date(TimeOfDayManager.getInstance().getCurrentTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        this.response.appendString(timeFormat.format(currentTime).toLowerCase());
        return this.response;
    }
}
