package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class IgnoredUsersMessageComposer extends MessageComposer {

    ArrayList<String> ignoredUsers;

    public IgnoredUsersMessageComposer(ArrayList<String> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.IgnoredUsersMessageComposer);
        this.response.appendInt(0);
        return this.response;
    }
}
