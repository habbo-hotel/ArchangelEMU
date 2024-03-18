package com.eu.habbo.habbohotel.items.interactions.games.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFreezeBlock extends RoomItem {
    public InteractionFreezeBlock(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionFreezeBlock(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (client == null)
            return;

        RoomItem item = null;
        RoomTile tile = room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
        THashSet<RoomItem> items = room.getRoomItemManager().getItemsAt(tile);

        for (RoomItem i : items) {
            if (i instanceof InteractionFreezeTile) {
                if (item == null || i.getCurrentZ() <= item.getCurrentZ()) {
                    item = i;
                }
            }
        }

        if (item != null) {
            FreezeGame game = (FreezeGame) room.getGame(FreezeGame.class);

            if (game == null)
                return;

            game.throwBall(client.getHabbo(), (InteractionFreezeTile) item);
        }
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtraData().length() == 0) {
            this.setExtraData("0");
        }
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return this.isWalkable();
    }

    @Override
    public boolean isWalkable() {
        return !this.getExtraData().isEmpty() && !this.getExtraData().equals("0");
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.getExtraData().isEmpty() || this.getExtraData().equalsIgnoreCase("0"))
            return;

        FreezeGame game = (FreezeGame) room.getGame(FreezeGame.class);
        if (game == null || !game.state.equals(GameState.RUNNING))
            return;

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo == null || habbo.getHabboInfo().getCurrentGame() != FreezeGame.class)
            return;

        FreezeGamePlayer player = (FreezeGamePlayer) habbo.getHabboInfo().getGamePlayer();

        if (player == null)
            return;

        int powerUp;
        try {
            powerUp = Integer.parseInt(this.getExtraData()) / 1000;
        } catch (NumberFormatException e) {
            powerUp = 0;
        }

        if (powerUp >= 2 && powerUp <= 7) {
            if (powerUp == 6 && !player.canPickupLife())
                return;

            this.setExtraData((powerUp + 10) * 1000 + "");

            room.updateItem(this);

            game.givePowerUp(player, powerUp);

            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FreezePowerUp"));
        }
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");
    }
}
