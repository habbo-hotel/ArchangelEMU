package com.eu.habbo.habbohotel.commands.credits;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;

import java.util.Map;

public class MassCreditsCommand extends BaseCreditsCommand {
    public MassCreditsCommand() {
        super("cmd_masscredits", Emulator.getTexts().getValue("commands.keys.cmd_masscredits").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            int amount;

            try {
                amount = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masscredits.invalid_amount"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (amount != 0) {
                for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                    Habbo habbo = set.getValue();

                    habbo.giveCredits(amount);
                    habbo.getClient().sendResponse(new CreditBalanceComposer(habbo));

                    if (habbo.getHabboInfo().getCurrentRoom() != null)
                        habbo.whisper(replaceAmount(getTextsValue("commands.generic.cmd_credits.received"), amount + ""), RoomChatMessageBubbles.ALERT);
                }
            }
            return true;
        }
        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masscredits.invalid_amount"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
