package com.eu.habbo.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.roleplay.guilds.GuildMember;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guild.GuildMembershipUpdatedMessageComposer;
import com.eu.habbo.plugin.events.guilds.GuildGivenAdminEvent;

public class AddAdminRightsToMemberEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild != null && (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermissionRight(Permission.ACC_GUILD_ADMIN))) {
            Emulator.getGameEnvironment().getGuildManager().setAdmin(guild, userId);

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            GuildGivenAdminEvent adminEvent = new GuildGivenAdminEvent(guild, userId, habbo, this.client.getHabbo());
            Emulator.getPluginManager().fireEvent(adminEvent);
            if (adminEvent.isCancelled())
                return;

            if (habbo != null) {
                Room room = habbo.getRoomUnit().getRoom();
                if (room != null) {
                    if (room.getRoomInfo().getGuild().getId() == guildId) {
                        room.getRoomRightsManager().refreshRightsForHabbo(habbo);
                    }
                }
            }

            GuildMember guildMember = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, userId);

            this.client.sendResponse(new GuildMembershipUpdatedMessageComposer(guild, guildMember));
        }
    }
}
