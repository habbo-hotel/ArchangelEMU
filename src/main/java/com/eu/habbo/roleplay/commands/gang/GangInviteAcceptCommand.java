package com.eu.habbo.roleplay.commands.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangManager;

public class GangInviteAcceptCommand extends Command {
    public GangInviteAcceptCommand() {
        super("cmd_gang_invite_accept");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getGang() != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_invite_user_already_in_gang"));
            return true;
        }


        String gangName = params[1];

        if (gangName == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.gang_not_found"));
            return true;
        }

        Gang targetedGang = GangManager.getInstance().getGangByName(gangName);

        if (targetedGang == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.gang_not_found"));
            return true;
        }

        if (targetedGang.getInvitedUser(gameClient.getHabbo()) == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_invite_missing"));
            return true;
        }


        gameClient.getHabbo().getHabboRoleplayStats().setGangID(targetedGang.getId());
        gameClient.getHabbo().getHabboRoleplayStats().setGangPositionID(targetedGang.getPositionByOrderID(1).getId());

        gameClient.getHabbo().getHabboRoleplayStats().getGang().removeInvitedUser(gameClient.getHabbo());

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_invite_accepted").replace("%gang%", targetedGang.getName()));

        return true;
    }
}