package com.eu.habbo.roleplay.commands.license;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.roleplay.corp.Corp;

public class LicenseConnectComputerCommand extends Command  {

    public LicenseConnectComputerCommand() {
        super("cmd_license_connect_computer");
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

        RoomItem item = gameClient.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemID);

        if (item == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.device.not_found"));
            return true;
        }

        int corpID = Integer.parseInt(params[2]);
        Corp corp = Emulator.getGameEnvironment().getCorpManager().getCorpByID(corpID);

        if (corp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.corp_not_found.not_found"));
            return true;
        }

        if (!gameClient.getHabbo().getHabboRoleplayStats().isWorking()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_at_work"));
            return true;
        }

        if (item.getOwnerInfo().getId() != gameClient.getHabbo().getHabboInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.device.not_setup"));
            return true;
        }

        item.setExtraData(String.valueOf(corpID));
        gameClient.getHabbo().getRoomUnit().getRoom().updateItemState(item);

        gameClient.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.device.new_connection")
                .replace(":corpName", corp.getGuild().getName())
        );

        return true;
    }
}
