package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildMemberFurniCountInHQMessageComposer;

public class GetMemberGuildItemCountEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null) return;
        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, this.client.getHabbo());
        if (userId == this.client.getHabbo().getHabboInfo().getId() || guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || (member != null && (member.getRank().equals(GuildRank.OWNER) || member.getRank().equals(GuildRank.ADMIN))) || this.client.getHabbo().hasRight(Permission.ACC_GUILD_ADMIN)) {
            Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(guild.getRoomId());
            int count = 0;
            if (room != null) {
                count = room.getUserFurniCount(userId);
            }
            this.client.sendResponse(new GuildMemberFurniCountInHQMessageComposer(userId, count));
        }


    }
}
