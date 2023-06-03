package com.eu.habbo.habbohotel.commands.list.badge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class BadgeCommand extends BaseBadgeCommand {
    public BadgeCommand() {
        super("cmd_badge");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_badge.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        if (params.length == 2) {
            gameClient.getHabbo().whisper(replaceUserAndBadge(getTextsValue("commands.error.cmd_badge.forgot_badge"), params[1], ""), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (params.length == 3) {
            Habbo habbo = getHabbo(params[1]);

            if (habbo != null) {
                if (habbo.addBadge(params[2])) {
                    gameClient.getHabbo().whisper(replaceUserAndBadge(getTextsValue("commands.succes.cmd_badge.given"), params[1], params[2]), RoomChatMessageBubbles.ALERT);
                } else {
                    gameClient.getHabbo().whisper(replaceUserAndBadge(getTextsValue("commands.error.cmd_badge.already_owned"), params[1], params[2]), RoomChatMessageBubbles.ALERT);
                }

                return true;
            } else {
                HabboInfo habboInfo = HabboManager.getOfflineHabboInfo(params[1]);

                if (habboInfo == null) {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_badge.unknown_user"), RoomChatMessageBubbles.ALERT);
                    return true;
                }

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                    boolean found;

                    try (PreparedStatement statement = connection.prepareStatement("SELECT `badge_code` FROM `users_badges` WHERE `user_id` = ? AND `badge_code` = ? LIMIT 1")) {
                        statement.setInt(1, habboInfo.getId());
                        statement.setString(2, params[2]);
                        try (ResultSet set = statement.executeQuery()) {
                            found = set.next();
                        }
                    }

                    if (found) {
                        gameClient.getHabbo().whisper(replaceUserAndBadge(getTextsValue("commands.error.cmd_badge.already_owns"), params[1], params[2]), RoomChatMessageBubbles.ALERT);
                    } else {
                        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges (`id`, `user_id`, `slot_id`, `badge_code`) VALUES (null, ?, 0, ?)")) {
                            statement.setInt(1, habboInfo.getId());
                            statement.setString(2, params[2]);
                            statement.execute();
                        }

                        gameClient.getHabbo().whisper(replaceUserAndBadge(getTextsValue("commands.succes.cmd_badge.given"), params[1], params[2]), RoomChatMessageBubbles.ALERT);
                    }
                    return true;
                } catch (SQLException e) {
                    log.error("Caught SQL exception", e);
                }
            }
        }

        return true;
    }
}
