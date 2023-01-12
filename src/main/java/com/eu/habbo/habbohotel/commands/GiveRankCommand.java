package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Rank;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import org.apache.commons.lang3.StringUtils;

public class GiveRankCommand extends Command {
    public GiveRankCommand() {
        super("cmd_give_rank", Emulator.getTexts().getValue("commands.keys.cmd_give_rank").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        Rank rank = null;
        switch (params.length) {
            case 1 -> {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_give_rank.missing_username") + getTextsValue("commands.description.cmd_give_rank"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            case 2 -> {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_give_rank.missing_rank") + getTextsValue("commands.description.cmd_give_rank"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            case 3 -> {
                if (StringUtils.isNumeric(params[2])) {
                    int rankId = Integer.parseInt(params[2]);
                    if (Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId))
                        rank = Emulator.getGameEnvironment().getPermissionsManager().getRank(rankId);
                } else {
                    rank = Emulator.getGameEnvironment().getPermissionsManager().getRankByName(params[2]);
                }
                if (rank != null) {
                    if (rank.getId() > gameClient.getHabbo().getHabboInfo().getRank().getId()) {
                        gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_give_rank.higher"), params[1]).replace("%id%", rank.getName()), RoomChatMessageBubbles.ALERT);
                        return true;
                    }

                    HabboInfo habbo = HabboManager.getOfflineHabboInfo(params[1]);

                    if (habbo != null) {
                        if (habbo.getRank().getId() > gameClient.getHabbo().getHabboInfo().getRank().getId()) {
                            gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_give_rank.higher.other"), params[1]).replace("%id%", rank.getName()), RoomChatMessageBubbles.ALERT);
                            return true;
                        }

                        Emulator.getGameEnvironment().getHabboManager().setRank(habbo.getId(), rank.getId());

                        gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_give_rank.updated"), params[1]).replace("%id%", rank.getName()), RoomChatMessageBubbles.ALERT);
                    } else {
                        gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_give_rank.user_offline"), params[1]).replace("%id%", rank.getName()), RoomChatMessageBubbles.ALERT);
                    }
                    return true;
                }
            }
        }

        gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.errors.cmd_give_rank.not_found"), params[1]).replace("%id%", params[2]), RoomChatMessageBubbles.ALERT);
        return true;
    }
}