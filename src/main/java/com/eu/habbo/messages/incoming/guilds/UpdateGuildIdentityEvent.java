package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.guilds.GuildChangedNameEvent;

public class UpdateGuildIdentityEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild != null) {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasRight(Permission.ACC_GUILD_ADMIN)) {
                GuildChangedNameEvent nameEvent = new GuildChangedNameEvent(guild, this.packet.readString(), this.packet.readString());
                Emulator.getPluginManager().fireEvent(nameEvent);

                if (nameEvent.isCancelled())
                    return;

                if (guild.getName().equals(nameEvent.getName()) && guild.getDescription().equals(nameEvent.getDescription()))
                    return;

                if(nameEvent.getName().length() > 29 || nameEvent.getDescription().length() > 254)
                    return;

                guild.setName(nameEvent.getName());
                guild.setDescription(nameEvent.getDescription());
                guild.needsUpdate = true;
                guild.run();

                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId());

                if (room != null && !room.getCurrentHabbos().isEmpty()) {
                    room.refreshGuild(guild);
                }
            }
        }
    }
}
