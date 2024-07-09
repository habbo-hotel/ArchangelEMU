package com.eu.habbo.roleplay.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.user.RoleplayPermissionsComposer;

public class RoleplayPermissionsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new RoleplayPermissionsComposer(this.client.getHabbo()));
    }
}