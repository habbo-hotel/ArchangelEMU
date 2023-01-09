package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.HabboInfo;

public class GuildAcceptedMembershipEvent extends GuildEvent {

    public final int userId;


    public final HabboInfo userInfo;


    public GuildAcceptedMembershipEvent(Guild guild, int userId, HabboInfo user) {
        super(guild);

        this.userId = userId;
        this.userInfo = user;
    }
}
