package com.eu.habbo.habbohotel.commands.list.bans;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class SuperbanCommand extends BaseBanCommand {
    public SuperbanCommand() {
        super("cmd_super_ban");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
       super.handle(gameClient, params);

        if (habboInfo != null) {
            if (habboInfo == gameClient.getHabbo().getHabboInfo()) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_super_ban.ban_self"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (habboInfo.getPermissionGroup().getId() >= gameClient.getHabbo().getHabboInfo().getPermissionGroup().getId()) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.target_rank_higher"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            count = Emulator.getGameEnvironment().getModToolManager().ban(habboInfo.getId(), gameClient.getHabbo(), reason, TEN_YEARS, ModToolBanType.SUPER, -1).size();
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.user_offline"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_super_ban").replace("%count%", count + ""), RoomChatMessageBubbles.ALERT);

        return true;
    }
}