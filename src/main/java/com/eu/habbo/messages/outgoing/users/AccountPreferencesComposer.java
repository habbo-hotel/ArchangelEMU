package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountPreferencesComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.accountPreferencesComposer);
        this.response.appendInt(this.habbo.getHabboStats().getVolumeSystem());
        this.response.appendInt(this.habbo.getHabboStats().getVolumeFurni());
        this.response.appendInt(this.habbo.getHabboStats().getVolumeTrax());
        this.response.appendBoolean(this.habbo.getHabboStats().isPreferOldChat());
        this.response.appendBoolean(this.habbo.getHabboStats().isBlockRoomInvites());
        this.response.appendBoolean(this.habbo.getHabboStats().isBlockCameraFollow());
        this.response.appendInt(this.habbo.getHabboStats().getUiFlags());
        this.response.appendInt(this.habbo.getHabboStats().getChatColor().getType());
        return this.response;
    }
}
