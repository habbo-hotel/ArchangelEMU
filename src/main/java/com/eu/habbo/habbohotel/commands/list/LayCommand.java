package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
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

        gameClient.getHabbo().getRoomUnit().setCmdLayEnabled(true);
        gameClient.getHabbo().getRoomUnit().getRoom().updateRoomUnit(gameClient.getHabbo().getRoomUnit());
        gameClient.getHabbo().getRoomUnit().setCmdSitEnabled(true);
        gameClient.getHabbo().getRoomUnit().setBodyRotation(RoomRotation.values()[gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() - gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() % 2]);

        RoomTile tile = gameClient.getHabbo().getRoomUnit().getCurrentPosition();
        if (tile == null) {
            return false;
        }

        if (IntStream.range(0, 3)
                .mapToObj(i -> gameClient.getHabbo().getRoomUnit().getRoom().getLayout().getTileInFront(tile, gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue(), i))
                .anyMatch(t -> t == null || !t.isWalkable())) {
            return false;
        }

        gameClient.getHabbo().getRoomUnit().addStatus(RoomUnitStatus.LAY, 0.5 + "");
        gameClient.getHabbo().getRoomUnit().getRoom().sendComposer(new UserUpdateComposer(gameClient.getHabbo().getRoomUnit()).compose());
        return true;
    }
}