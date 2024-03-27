package com.eu.habbo.roleplay.commands.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.database.GangRepository;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangManager;

public class GangDisbandCommand extends Command {
    public GangDisbandCommand() {
        super("cmd_gang_disband");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getGang() == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.not_in_a_gang"));
            return true;
        }

        if (gameClient.getHabbo().getHabboInfo().getId() != gameClient.getHabbo().getHabboRoleplayStats().getGang().getUserID()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_disband_not_allowed"));
            return true;
        }

        Gang userGang = gameClient.getHabbo().getHabboRoleplayStats().getGang();

        GangManager.getInstance().deleteGang(userGang);

        gameClient.getHabbo().getHabboRoleplayStats().setGang(null, null);

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_disband_success"));

        return true;
    }
}