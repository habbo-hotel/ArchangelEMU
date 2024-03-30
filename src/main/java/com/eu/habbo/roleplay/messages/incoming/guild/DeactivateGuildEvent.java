package com.eu.habbo.roleplay.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.roleplay.guilds.GuildMember;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.FavoriteMembershipUpdateMessageComposer;
import com.eu.habbo.roleplay.messages.outgoing.guild.HabboGroupDeactivatedMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.GetGuestRoomResultComposer;
import com.eu.habbo.plugin.events.guilds.GuildDeletedEvent;
import gnu.trove.set.hash.THashSet;

public class DeactivateGuildEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild != null) {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermissionRight(Permission.ACC_GUILD_ADMIN)) //TODO Add staff permission override.
            {
                THashSet<GuildMember> members = Emulator.getGameEnvironment().getGuildManager().getGuildMembers(guild.getId());

                for (GuildMember member : members) {
                    Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(member.getUserId());
                    if (habbo != null)
                        if (habbo.getRoomUnit().getRoom() != null && habbo.getRoomUnit() != null)
                            habbo.getRoomUnit().getRoom().sendComposer(new FavoriteMembershipUpdateMessageComposer(habbo.getRoomUnit(), null).compose());
                }

                Emulator.getGameEnvironment().getGuildManager().deleteGuild(guild);
                Emulator.getPluginManager().fireEvent(new GuildDeletedEvent(guild, this.client.getHabbo()));
                Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(guild.getRoomId()).sendComposer(new HabboGroupDeactivatedMessageComposer(guildId).compose());

                if (this.client.getHabbo().getRoomUnit().getRoom() != null) {
                    if (guild.getRoomId() == this.client.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId()) {
                        this.client.sendResponse(new GetGuestRoomResultComposer(this.client.getHabbo().getRoomUnit().getRoom(), this.client.getHabbo(), false, false));
                    }
                }
            }
        }
    }
}
