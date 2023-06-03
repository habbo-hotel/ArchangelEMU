package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;


public class FacelessCommand extends Command {
    public FacelessCommand() {
        super("cmd_faceless");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return false;
        }

        try {

            String[] figureParts = gameClient.getHabbo().getHabboInfo().getLook().split("\\.");

            for (String part : figureParts) {
                if (part.startsWith("hd")) {
                    String[] headParts = part.split("-");

                    if (!headParts[1].equals("99999"))
                        headParts[1] = "99999";
                    else
                        break;

                    String newHead = "hd-" + headParts[1] + "-" + headParts[2];

                    gameClient.getHabbo().getHabboInfo().setLook(gameClient.getHabbo().getHabboInfo().getLook().replace(part, newHead));
                    gameClient.sendResponse(new FigureUpdateComposer(gameClient.getHabbo()));
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserChangeMessageComposer(gameClient.getHabbo()).compose());
                    return true;
                }
            }

        } catch (Exception ignored) {

        }

        return false;
    }
}
