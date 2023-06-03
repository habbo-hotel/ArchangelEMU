package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;

public class CoordsCommand extends Command {
    public CoordsCommand() {
        super("cmd_coords");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getRoomUnit() == null || gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return false;

        if (params.length == 1) {
            gameClient.getHabbo().alert(getTextsValue("commands.generic.cmd_coords.title") + "\r\n" +
                    "x: " + gameClient.getHabbo().getRoomUnit().getX() + "\r" +
                    "y: " + gameClient.getHabbo().getRoomUnit().getY() + "\r" +
                    "z: " + (gameClient.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT) ? gameClient.getHabbo().getRoomUnit().getStatus(RoomUnitStatus.SIT) : gameClient.getHabbo().getRoomUnit().getZ()) + "\r" +
                    getTextsValue("generic.rotation.head") + ": " + gameClient.getHabbo().getRoomUnit().getHeadRotation() + "-" + gameClient.getHabbo().getRoomUnit().getHeadRotation().getValue() + "\r" +
                    getTextsValue("generic.rotation.body") + ": " + gameClient.getHabbo().getRoomUnit().getBodyRotation() + "-" + gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() + "\r" +
                    getTextsValue("generic.sitting") + ": " + (gameClient.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT) ? getTextsValue("generic.yes") : getTextsValue("generic.no")) + "\r" +
                    "Tile State: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).getState().name() + "\r" +
                    "Tile Walkable: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).isWalkable() + "\r" +
                    "Tile relative height: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).relativeHeight() + "\r" +
                    "Tile stack height: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).getStackHeight());

        } else {
            RoomTile tile = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(Short.parseShort(params[1]), Short.parseShort(params[2]));

            if (tile != null) {
                gameClient.getHabbo().alert(getTextsValue("commands.generic.cmd_coords.title") + "\r\n" +
                        "x: " + tile.getX() + "\r" +
                        "y: " + tile.getY() + "\r" +
                        "z: " + tile.getZ() + "\r" +
                        "Tile State: " + tile.getState().name() + "\r" +
                        "Tile Relative Height: " + tile.relativeHeight() + "\r" +
                        "Tile Stack Height: " + tile.getStackHeight() + "\r" +
                        "Tile Walkable: " + (tile.isWalkable() ? "Yes" : "No") + "\r");
            } else {
                gameClient.getHabbo().whisper(getTextsValue("generic.tile.not.exists"));
            }
        }
        return true;
    }
}
