package com.eu.habbo.habbohotel.commands.bans;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.IPBanCommand;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class SuperbanCommand extends BaseBanCommand {
    public SuperbanCommand() {
        super("cmd_super_ban", Emulator.getTexts().getValue("commands.keys.cmd_super_ban").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
       super.handle(gameClient, params);

        if (habboInfo != null) {
            if (habboInfo == gameClient.getHabbo().getHabboInfo()) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_super_ban.ban_self"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (habboInfo.getRank().getId() >= gameClient.getHabbo().getHabboInfo().getRank().getId()) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_ban.target_rank_higher"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            count = Emulator.getGameEnvironment().getModToolManager().ban(habboInfo.getId(), gameClient.getHabbo(), reason, IPBanCommand.TEN_YEARS, ModToolBanType.SUPER, -1).size();
        } else {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_ban.user_offline"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_super_ban").replace("%count%", count + ""), RoomChatMessageBubbles.ALERT);

        return true;
    }
}