package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiPuck extends InteractionPushable {
    public InteractionBattleBanzaiPuck(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBattleBanzaiPuck(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public int getWalkOnVelocity(RoomUnit roomUnit, Room room) {
        return 6;
    }

    @Override
    public RoomRotation getWalkOnDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getWalkOffVelocity(RoomUnit roomUnit, Room room) {
        return 0;
    }

    @Override
    public RoomRotation getWalkOffDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getDragVelocity(RoomUnit roomUnit, Room room) {
        return 1;
    }

    @Override
    public RoomRotation getDragDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getTackleVelocity(RoomUnit roomUnit, Room room) {
        return 6;
    }

    @Override
    public RoomRotation getTackleDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getNextRollDelay(int currentStep, int totalSteps) {
        return (totalSteps == 1) ? 500 : 100 + (currentStep * 100);
    }

    @Override
    public RoomRotation getBounceDirection(Room room, RoomRotation currentDirection) {
        switch (currentDirection) {
            default:
            case NORTH:
                return RoomRotation.SOUTH;

            case NORTH_EAST:
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.NORTH_WEST.getValue())))
                    return RoomRotation.NORTH_WEST;
                else if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.SOUTH_EAST.getValue())))
                    return RoomRotation.SOUTH_EAST;
                else
                    return RoomRotation.SOUTH_WEST;

            case EAST:
                return RoomRotation.WEST;

            case SOUTH_EAST:
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.SOUTH_WEST.getValue())))
                    return RoomRotation.SOUTH_WEST;
                else if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.NORTH_EAST.getValue())))
                    return RoomRotation.NORTH_EAST;
                else
                    return RoomRotation.NORTH_WEST;

            case SOUTH:
                return RoomRotation.NORTH;

            case SOUTH_WEST:
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.SOUTH_EAST.getValue())))
                    return RoomRotation.SOUTH_EAST;
                else if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.NORTH_WEST.getValue())))
                    return RoomRotation.NORTH_WEST;
                else
                    return RoomRotation.NORTH_EAST;

            case WEST:
                return RoomRotation.EAST;

            case NORTH_WEST:
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.NORTH_EAST.getValue())))
                    return RoomRotation.NORTH_EAST;
                else if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomRotation.SOUTH_WEST.getValue())))
                    return RoomRotation.SOUTH_WEST;
                else
                    return RoomRotation.SOUTH_EAST;
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
    }

    @Override
    public boolean validMove(Room room, RoomTile from, RoomTile to) {
       if (to == null) return false;
       RoomItem topItem = room.getTopItemAt(to.getX(), to.getY(), this);
       return !(!room.getLayout().tileWalkable(to.getX(), to.getY()) || (topItem != null && (!topItem.getBaseItem().allowStack() || topItem.getBaseItem().allowSit() || topItem.getBaseItem().allowLay())));
       
        //return !(!room.getLayout().tileWalkable(to.x, to.y) || (topItem != null && (!topItem.getBaseItem().setAllowStack() || topItem.getBaseItem().allowSit() || topItem.getBaseItem().allowLay())));
    }

    @Override
    public void onDrag(Room room, RoomUnit roomUnit, int velocity, RoomRotation direction) {

    }

    @Override
    public void onKick(Room room, RoomUnit roomUnit, int velocity, RoomRotation direction) {

    }

    @Override
    public void onTackle(Room room, RoomUnit roomUnit, int velocity, RoomRotation direction) {

    }

    @Override
    public void onMove(Room room, RoomTile from, RoomTile to, RoomRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(kicker);

        if (habbo != null) {
            BattleBanzaiGame game = (BattleBanzaiGame) room.getGame(BattleBanzaiGame.class);
            if (game != null) {
                GameTeam team = game.getTeamForHabbo(habbo);
                if (team != null) {
                    RoomItem item = room.getTopItemAt(to.getX(), to.getY());
                        try {
                            item.onWalkOn(kicker, room, null);
                        } catch (Exception e) {
                            return;
                        }
                    this.setExtradata(team.teamColor.type + "");
                    room.updateItemState(this);
                }
            }
        }
        //TODO Implement point counting logic.
    }

    @Override
    public void onBounce(Room room, RoomRotation oldDirection, RoomRotation newDirection, RoomUnit kicker) {

    }

    @Override
    public void onStop(Room room, RoomUnit kicker, int currentStep, int totalSteps) {

    }

    @Override
    public boolean canStillMove(Room room, RoomTile from, RoomTile to, RoomRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        return to.getState() == RoomTileState.OPEN && to.isWalkable();
    }
}
