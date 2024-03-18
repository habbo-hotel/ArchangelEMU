package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AllowTradingCommand extends Command {
    public AllowTradingCommand() {
        super("cmd_allow_trading");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length == 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_allow_trading.forgot_username"));
            return true;
        }

        if (params.length == 2) {
            gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_allow_trading.forgot_trade"), params[1]));
            return true;
        }

        final String username = params[1];
        final String option = params[2];

        if (option.equalsIgnoreCase(getTextsValue("generic.yes")) || option.equalsIgnoreCase(getTextsValue("generic.no"))) {
            final boolean enabled = option.equalsIgnoreCase(getTextsValue("generic.yes"));
            final Habbo habbo = getHabbo(username);

            if (habbo != null) {
                if (!enabled) {
                    try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                         PreparedStatement statement = connection.prepareStatement("UPDATE users_settings SET tradelock_amount = tradelock_amount + 1 WHERE user_id = ?")) {
                        statement.setInt(1, habbo.getHabboInfo().getId());
                        statement.executeUpdate();
                    }
                }
                habbo.getHabboStats().setAllowTrade(enabled);
                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_allow_trading." + (enabled ? "enabled" : "disabled")), params[1]));
                habbo.getClient().sendResponse(new UserPerksComposer(habbo));
                return true;
            } else {
                boolean found;
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE users_settings INNER JOIN users ON users.id = users_settings.user_id SET can_trade = ?, tradelock_amount = tradelock_amount + ? WHERE users.username LIKE ?")) {
                    statement.setString(1, booleanToIntString(enabled));
                    statement.setInt(2, booleanToInt(enabled));
                    statement.setString(3, username);
                    found = statement.executeUpdate() > 0;
                }

                if (!found) {
                    gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_allow_trading.user_not_found"), params[1]));
                    return true;
                }

                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_allow_trading." + (enabled ? "enabled" : "disabled")), params[1]));
            }
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_allow_trading.incorrect_setting").replace("%enabled%", getTextsValue("generic.yes")).replace("%disabled%", getTextsValue("generic.no")));
        }
        return true;
    }
}
