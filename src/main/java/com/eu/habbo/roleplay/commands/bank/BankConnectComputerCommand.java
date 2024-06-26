package com.eu.habbo.roleplay.commands.bank;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.roleplay.corp.Corp;

public class BankConnectComputerCommand extends Command  {

    public BankConnectComputerCommand() {
        super("cmd_bank_connect_computer");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        if (params.length != 3) {
            return true;
        }

        int itemID = Integer.parseInt(params[1]);
        int corpID = Integer.parseInt(params[2]);

        RoomItem item = gameClient.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemID);
        Corp corp = Emulator.getGameEnvironment().getCorpManager().getCorpByID(corpID);

        if (item == null || corp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.connect_computer.not_found"));
            return true;
        }

        if (item.getOwnerInfo().getId() != gameClient.getHabbo().getHabboInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.connect_computer.not_allowed"));
            return true;
        }

        item.setExtraData(String.valueOf(corpID));
        gameClient.getHabbo().getRoomUnit().getRoom().updateItemState(item);

        gameClient.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.bank.connect_computer.success")
                .replace(":bankName", corp.getGuild().getName())
        );

        return true;
    }
}
