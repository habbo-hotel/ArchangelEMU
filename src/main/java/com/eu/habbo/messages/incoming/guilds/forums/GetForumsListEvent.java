package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.ForumsListMessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Slf4j
public class GetForumsListEvent extends MessageHandler {

    @Override
    public void handle() {
        int mode = this.packet.readInt();
        int offset = this.packet.readInt();
        int amount = this.packet.readInt();

        Set<Guild> guilds = switch (mode) {
            case 0 -> // most active
                    getActiveForums();
            case 1 -> // most viewed
                    Emulator.getGameEnvironment().getGuildManager().getMostViewed();
            case 2 -> // my groups
                    getMyForums(this.client.getHabbo().getHabboInfo().getId());
            default -> null;
        };

        if (guilds != null) {
            this.client.sendResponse(new ForumsListMessageComposer(guilds, this.client.getHabbo(), mode, offset));
        }
    }

    private THashSet<Guild> getActiveForums() {
        THashSet<Guild> guilds = new THashSet<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT `guilds`.`id`, SUM(`guilds_forums_threads`.`posts_count`) AS `post_count` " +
                "FROM `guilds_forums_threads` " +
                "LEFT JOIN `guilds` ON `guilds`.`id` = `guilds_forums_threads`.`guild_id` " +
                "WHERE `guilds`.`read_forum` = 'EVERYONE' AND `guilds_forums_threads`.`created_at` > ? " +
                "GROUP BY `guilds`.`id` " +
                "ORDER BY `post_count` DESC LIMIT 100")) {
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 7 * 24 * 60 * 60);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(set.getInt("id"));

                if (guild != null) {
                    guilds.add(guild);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
            this.client.sendResponse(new ErrorReportComposer(500));
        }

        return guilds;
    }

    private THashSet<Guild> getMyForums(int userId) {
        THashSet<Guild> guilds = new THashSet<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT `guilds`.`id` FROM `guilds_members` " +
                "LEFT JOIN `guilds` ON `guilds`.`id` = `guilds_members`.`guild_id` " +
                "WHERE `guilds_members`.`user_id` = ? AND `guilds`.`forum` = '1'")) {
            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(set.getInt("id"));

                if (guild != null) {
                    guilds.add(guild);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
            this.client.sendResponse(new ErrorReportComposer(500));
        }

        return guilds;
    }
}