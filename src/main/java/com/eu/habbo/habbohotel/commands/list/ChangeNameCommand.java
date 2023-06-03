package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserObjectComposer;

public class ChangeNameCommand extends Command {
    public ChangeNameCommand() {
        super("cmd_change_name");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        // check if there are no params
        if (params.length < 2) {
            gameClient.getHabbo().getHabboStats().setAllowNameChange(!gameClient.getHabbo().getHabboStats().isAllowNameChange());
            gameClient.sendResponse(new UserObjectComposer(gameClient.getHabbo()));
            return true;
        }

        // check if the habbo exists or is online
        Habbo habbo = getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_changename.user_not_found"), params[1]));
            return true;
        }

        // this runs if params[1] is a valid habbo
        gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_changename.done"), params[1]));
        habbo.alert(getTextsValue("commands.succes.cmd_changename.received"));
        habbo.getHabboStats().setAllowNameChange(!habbo.getHabboStats().isAllowNameChange());
        habbo.getClient().sendResponse(new UserObjectComposer(habbo));
        return true;
    }
}
