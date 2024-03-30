package com.eu.habbo.roleplay.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.FavoriteMembershipUpdateMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.HabboAddGroupBadgesMessageComposer;
import com.eu.habbo.messages.outgoing.users.ExtendedProfileMessageComposer;
import com.eu.habbo.plugin.events.guilds.GuildFavoriteSetEvent;

public class SelectFavouriteHabboGroupEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (this.client.getHabbo().getHabboStats().hasGuild(guildId)) {
            GuildFavoriteSetEvent favoriteSetEvent = new GuildFavoriteSetEvent(guild, this.client.getHabbo());
            Emulator.getPluginManager().fireEvent(favoriteSetEvent);

            if (favoriteSetEvent.isCancelled())
                return;

            this.client.getHabbo().getHabboStats().setGuild(guildId);

            if (this.client.getHabbo().getRoomUnit().getRoom() != null) {
                if (guild != null) {
                    this.client.getHabbo().getRoomUnit().getRoom().sendComposer(new HabboAddGroupBadgesMessageComposer(guild).compose());
                    this.client.getHabbo().getRoomUnit().getRoom().sendComposer(new FavoriteMembershipUpdateMessageComposer(this.client.getHabbo().getRoomUnit(), guild).compose());
                }
            }

            this.client.sendResponse(new ExtendedProfileMessageComposer(this.client.getHabbo(), this.client));
        }
    }
}
