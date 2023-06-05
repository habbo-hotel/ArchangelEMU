package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class BlockAlertCommand extends Command {
    public BlockAlertCommand() {
        super("cmd_block_alert");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            gameClient.getHabbo().getHabboStats().setBlockStaffAlerts(!gameClient.getHabbo().getHabboStats().isBlockStaffAlerts());
            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_blockalert").replace("%state%", (gameClient.getHabbo().getHabboStats().isBlockStaffAlerts() ? getTextsValue("generic.on") : getTextsValue("generic.off"))), RoomChatMessageBubbles.ALERT);

            return true;
        }

        return false;
    }
}
