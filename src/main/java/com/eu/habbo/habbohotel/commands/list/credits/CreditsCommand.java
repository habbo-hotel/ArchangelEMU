package com.eu.habbo.habbohotel.commands.list.credits;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;

public class CreditsCommand extends BaseCreditsCommand {
    private static final String INVALID_AMOUNT = "commands.error.cmd_credits.invalid_amount";

    public CreditsCommand() {
        super("cmd_credits");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 3) {
            HabboInfo info = HabboManager.getOfflineHabboInfo(params[1]);

            if (info != null) {
                Habbo habbo = getHabbo(params[1]);

                int credits;
                try {
                    credits = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                if (habbo != null) {
                    if (credits != 0) {
                        habbo.giveCredits(credits);
                        if (habbo.getHabboInfo().getCurrentRoom() != null)
                            habbo.whisper(replaceAmount(getTextsValue("commands.generic.cmd_credits.received"), params[2]), RoomChatMessageBubbles.ALERT);
                        else
                            habbo.alert(replaceAmount(getTextsValue("commands.generic.cmd_credits.received"), params[2]));

                        gameClient.getHabbo().whisper(replaceUserAndAmount(getTextsValue("commands.succes.cmd_credits.send"), params[1], params[2]), RoomChatMessageBubbles.ALERT);

                    } else {
                        gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT), RoomChatMessageBubbles.ALERT);
                    }
                } else {
                    Emulator.getGameEnvironment().getHabboManager().giveCredits(info.getId(), credits);
                    gameClient.getHabbo().whisper(replaceUserAndAmount(getTextsValue("commands.succes.cmd_credits.send"), params[1], params[2]), RoomChatMessageBubbles.ALERT);

                }
            } else {
                gameClient.getHabbo().whisper(replaceUserAndAmount(getTextsValue("commands.error.cmd_credits.user_not_found"), params[1], params[2]), RoomChatMessageBubbles.ALERT);
            }
        } else {
            gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
