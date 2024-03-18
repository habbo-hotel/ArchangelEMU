package com.eu.habbo.habbohotel.guilds;

import com.eu.habbo.database.DatabaseConstants;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildMember implements Comparable<GuildMember> {
    @Getter
    private final int userId;
    @Getter
    private final String username;
    @Getter
    @Setter
    private String look;
    @Getter
    @Setter
    private int joinDate;
    @Setter
    @Getter
    private GuildRank rank;

    public GuildMember(ResultSet set) throws SQLException {
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.username = set.getString("username");
        this.look = set.getString("look");
        this.joinDate = set.getInt("member_since");
        this.rank = GuildRank.getRank(set.getInt("level_id"));
    }

    public GuildMember(int user_id, String username, String look, int joinDate, int guildRank) {
        this.userId = user_id;
        this.username = username;
        this.look = look;
        this.joinDate = joinDate;
        this.rank = GuildRank.values()[guildRank];
    }

    @Override
    public int compareTo(GuildMember o) {
        return this.userId - o.userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GuildMember) {
            return ((GuildMember) o).userId == this.userId && ((GuildMember) o).joinDate == this.joinDate && ((GuildMember) o).rank == this.rank;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.userId;
    }

    public GuildMembershipStatus getMembershipStatus() {
        if (this.rank == GuildRank.DELETED) return GuildMembershipStatus.NOT_MEMBER;
        if (this.rank == GuildRank.OWNER || this.rank == GuildRank.ADMIN || this.rank == GuildRank.MEMBER) return GuildMembershipStatus.MEMBER;
        if (this.rank == GuildRank.REQUESTED) return GuildMembershipStatus.PENDING;

        return GuildMembershipStatus.NOT_MEMBER;
    }
}
