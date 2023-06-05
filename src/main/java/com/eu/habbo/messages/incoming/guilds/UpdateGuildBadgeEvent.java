package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.plugin.events.guilds.GuildChangedBadgeEvent;

public class UpdateGuildBadgeEvent extends GuildBadgeEvent {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if (guild != null) {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasRight(Permission.ACC_GUILD_ADMIN)) {
                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId());

                if (room == null || room.getId() != guild.getRoomId())
                    return;

                int count = this.packet.readInt();

                StringBuilder badge = createBadge(count);

                if (guild.getBadge().equalsIgnoreCase(badge.toString()))
                    return;

                GuildChangedBadgeEvent badgeEvent = new GuildChangedBadgeEvent(guild, badge.toString());
                Emulator.getPluginManager().fireEvent(badgeEvent);

                if (badgeEvent.isCancelled())
                    return;

                guild.setBadge(badgeEvent.getBadge());
                guild.needsUpdate = true;

                if (Emulator.getConfig().getBoolean("imager.internal.enabled")) {
                    Emulator.getBadgeImager().generate(guild);
                }

                room.refreshGuild(guild);
                Emulator.getThreading().run(guild);
            }
        }
    }


}
