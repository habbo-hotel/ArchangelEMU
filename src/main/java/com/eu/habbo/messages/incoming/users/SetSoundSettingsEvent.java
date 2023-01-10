package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.users.UserSavedSettingsEvent;

public class SetSoundSettingsEvent extends MessageHandler {
    @Override
    public void handle() {
        int system = this.packet.readInt();
        int furni = this.packet.readInt();
        int trax = this.packet.readInt();

        this.client.getHabbo().getHabboStats().setVolumeSystem(system);
        this.client.getHabbo().getHabboStats().setVolumeFurni(furni);
        this.client.getHabbo().getHabboStats().setVolumeTrax(trax);

        Emulator.getPluginManager().fireEvent(new UserSavedSettingsEvent(this.client.getHabbo()));
    }
}
