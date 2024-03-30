package com.eu.habbo.roleplay.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.roleplay.guilds.GuildState;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.HabboGroupDetailsMessageComposer;
import com.eu.habbo.roleplay.messages.outgoing.guild.HabboGroupJoinFailedMessageComposer;

public class JoinHabboGroupEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();

        if (this.client.getHabbo().getHabboStats().hasGuild(guildId))
            return;

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null)
            return;

        if (guild.getState() == GuildState.CLOSED || guild.getState() == GuildState.LARGE_CLOSED) {
            this.client.sendResponse(new HabboGroupJoinFailedMessageComposer(HabboGroupJoinFailedMessageComposer.GROUP_CLOSED));
            return;
        }

        Emulator.getGameEnvironment().getGuildManager().joinGuild(guild, this.client, 0, false);
        this.client.sendResponse(new HabboGroupDetailsMessageComposer(guild, this.client, false, Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, this.client.getHabbo())));

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null || room.getRoomInfo().getGuild().getId() != guildId)
            return;

        room.getRoomRightsManager().refreshRightsForHabbo(this.client.getHabbo());
    }
}
