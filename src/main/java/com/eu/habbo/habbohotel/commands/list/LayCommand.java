package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;

import java.util.stream.IntStream;

public class LayCommand extends Command {
    public LayCommand() {
        super("cmd_lay");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getRoomUnit() == null || !gameClient.getHabbo().getRoomUnit().canForcePosture())
            return true;

        gameClient.getHabbo().getRoomUnit().setCmdLay(true);
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().updateHabbo(gameClient.getHabbo());
        gameClient.getHabbo().getRoomUnit().setCmdSit(true);
        gameClient.getHabbo().getRoomUnit().setBodyRotation(RoomUserRotation.values()[gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() - gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() % 2]);

        RoomTile tile = gameClient.getHabbo().getRoomUnit().getCurrentLocation();
        if (tile == null) {
            return false;
        }

        if (IntStream.range(0, 3)
                .mapToObj(i -> gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(tile, gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue(), i))
                .anyMatch(t -> t == null || !t.isWalkable())) {
            return false;
        }

        gameClient.getHabbo().getRoomUnit().setStatus(RoomUnitStatus.LAY, 0.5 + "");
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserUpdateComposer(gameClient.getHabbo().getRoomUnit()).compose());
        return true;
    }
}