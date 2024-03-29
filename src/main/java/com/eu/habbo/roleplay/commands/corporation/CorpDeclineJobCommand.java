package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.corps.Corporation;
import com.eu.habbo.roleplay.corps.CorporationManager;

public class CorpDeclineJobCommand extends Command {
    public CorpDeclineJobCommand() {
        super("cmd_corp_declinejob");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        String corpName = params[1];

        if (corpName == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.corp_not_found"));
            return true;
        }

        Corporation targetedCorp = CorporationManager.getInstance().getCorporationByName(corpName);

        if (targetedCorp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.corp_not_found"));
            return true;
        }

        if (targetedCorp.getInvitedUser(gameClient.getHabbo()) == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_corp_invite_missing"));
            return true;
        }

        targetedCorp.removeInvitedUser(gameClient.getHabbo());

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_corp_invite_rejected").replace("%corp%", targetedCorp.getName()));

        return true;
    }
}
