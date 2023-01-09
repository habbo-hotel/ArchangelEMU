package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FollowFriendFailedComposer extends MessageComposer {
    public static final int NOT_IN_FRIEND_LIST = 0;
    public static final int FRIEND_OFFLINE = 1;
    public static final int FRIEND_NOT_IN_ROOM = 2;
    public static final int FRIEND_BLOCKED_STALKING = 3;

    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.followFriendFailedComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
