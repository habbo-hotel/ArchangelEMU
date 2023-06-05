package com.eu.habbo.habbohotel.commands.list.bans;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BanCommand extends Command {
    public BanCommand() {
        super("cmd_ban");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.forgot_user"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (params.length < 3) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.forgot_time"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        int banTime;
        try {
            banTime = Integer.parseInt(params[2]);
        } catch (Exception e) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.invalid_time"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (banTime < 600) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.time_to_short"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (params[1].equalsIgnoreCase(gameClient.getHabbo().getHabboInfo().getUsername())) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.ban_self"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo t = getHabbo(params[1]);

        HabboInfo target;
        if (t != null) {
            target = t.getHabboInfo();
        } else {
            target = HabboManager.getOfflineHabboInfo(params[1]);
        }

        if (target == null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.user_offline"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (target.getPermissionGroup().getId() >= gameClient.getHabbo().getHabboInfo().getPermissionGroup().getId()) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ban.target_rank_higher"), RoomChatMessageBubbles.ALERT);
            return true;
        }


        String reason = "";

        if (params.length > 3) {
            reason = IntStream.range(3, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());
        }

        ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().ban(target.getId(), gameClient.getHabbo(), reason, banTime, ModToolBanType.ACCOUNT, -1).get(0);
        gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_ban.ban_issued"), target.getUsername()).replace("%time%", ban.getExpireDate() - Emulator.getIntUnixTimestamp() + "").replace("%reason%", ban.getReason()), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
