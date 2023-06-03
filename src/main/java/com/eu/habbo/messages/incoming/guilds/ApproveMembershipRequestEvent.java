package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildMemberMgmtFailedMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildMembershipRejectedMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.HabboGroupDetailsMessageComposer;
import com.eu.habbo.plugin.events.guilds.GuildAcceptedMembershipEvent;

public class ApproveMembershipRequestEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        HabboInfo userInfo;
        if (guild == null) return;
        GuildMember groupMember = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, this.client.getHabbo());

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        if (habbo == null) userInfo = HabboManager.getOfflineHabboInfo(userId);
        else userInfo = habbo.getHabboInfo();


        if (userInfo == null || groupMember == null || userId == this.client.getHabbo().getHabboInfo().getId() || (!this.client.getHabbo().hasRight(Permission.ACC_GUILD_ADMIN) && guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId() && !groupMember.getRank().equals(GuildRank.ADMIN) && !groupMember.getRank().equals(GuildRank.OWNER)))
            return;

        if (!userInfo.getHabboStats().hasGuild(guild.getId())) {
            this.client.sendResponse(new GuildMemberMgmtFailedMessageComposer(guild.getId(), GuildMemberMgmtFailedMessageComposer.ALREADY_ACCEPTED));
            return;
        }

        if (!Emulator.getGameEnvironment().getGuildManager().joinGuild(guild, this.client, userId, true)) return;
        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild.getId(), userId);

        if (member == null || member.getRank().getType() != GuildRank.REQUESTED.getType()) {
            this.client.sendResponse(new GuildMemberMgmtFailedMessageComposer(guild.getId(), GuildMemberMgmtFailedMessageComposer.NO_LONGER_MEMBER));
            return;
        }
        GuildAcceptedMembershipEvent event = new GuildAcceptedMembershipEvent(guild, userId, userInfo);
        Emulator.getPluginManager().fireEvent(event);
        if (event.isCancelled()) return;

        userInfo.getHabboStats().addGuild(guild.getId());
        boolean joinGuild = Emulator.getGameEnvironment().getGuildManager().joinGuild(guild, this.client, userId, true);
        if (!joinGuild)
            guild.decreaseRequestCount();
        guild.increaseMemberCount();
        this.client.sendResponse(new GuildMembershipRejectedMessageComposer(guild, userId));

        if (habbo != null && userInfo.isOnline() && userInfo.getCurrentRoom() != null && userInfo.getCurrentRoom().getGuildId() == guildId) {
            habbo.getClient().sendResponse(new HabboGroupDetailsMessageComposer(guild, habbo.getClient(), false, Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, userId)));
            userInfo.getCurrentRoom().refreshRightsForHabbo(habbo);
        }
    }
}
