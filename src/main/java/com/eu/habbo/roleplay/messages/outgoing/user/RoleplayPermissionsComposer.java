package com.eu.habbo.roleplay.messages.outgoing.user;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoleplayPermissionsComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roleplayPermissionsComposer);
        this.response.appendBoolean(this.habbo.hasPermissionRight(Permission.ACC_NAVIGATOR_SHOW_ALL));
        return this.response;
    }
}
