package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConcurrentUsersGoalProgressMessageComposer extends MessageComposer {
    public static final int ACTIVE = 0;
    public static final int HIDDEN = 2;
    public static final int ACHIEVED = 3;

    private final int state;
    private final int userCount;
    private final int goal;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.concurrentUsersGoalProgressMessageComposer);
        this.response.appendInt(this.state);
        this.response.appendInt(this.userCount);
        this.response.appendInt(this.goal);
        return this.response;
    }
}
