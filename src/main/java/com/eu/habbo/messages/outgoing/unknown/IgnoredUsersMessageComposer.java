package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class IgnoredUsersMessageComposer extends MessageComposer {

    private final ArrayList<String> ignoredUsers;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.ignoredUsersMessageComposer);
        this.response.appendInt(0);
        return this.response;
    }
}
