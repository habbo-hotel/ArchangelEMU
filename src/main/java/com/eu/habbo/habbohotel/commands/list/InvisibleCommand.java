package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;

public class InvisibleCommand extends Command {
    public InvisibleCommand() {
        super("cmd_invisible");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        RoomUnit roomUnit = gameClient.getHabbo().getRoomUnit();

        if (roomUnit.isInvisible()) {
            RoomLayout roomLayout = roomUnit.getRoom().getLayout();

            new RoomUnitTeleport(roomUnit, roomUnit.getRoom(), roomLayout.getDoorTile().getX(), roomLayout.getDoorTile().getY(), roomLayout.getDoorTile().getZ(), 0).run();

            roomUnit.setInvisible(false)
                    .setInRoom(true);

            roomUnit.getRoom().sendComposer(new RoomUsersComposer(gameClient.getHabbo()).compose());
            roomUnit.getRoom().sendComposer(new UserUpdateComposer(roomUnit).compose());

            WiredHandler.handle(WiredTriggerType.ENTER_ROOM, roomUnit, roomUnit.getRoom(), null);
            roomUnit.getRoom().habboEntered(gameClient.getHabbo());

            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_invisible.updated.back"));

            return true;
        }

        roomUnit.setInvisible(true);
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_invisible.updated"));
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserRemoveMessageComposer(roomUnit).compose());

        return true;
    }
}
