package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectLeaveTeam extends InteractionWiredEffect {
    public WiredEffectLeaveTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectLeaveTeam(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo != null) {
            if (habbo.getHabboInfo().getCurrentGame() != null) {
                Game game = room.getGame(habbo.getHabboInfo().getCurrentGame());

                if (game == null) {
                    game = room.getGameOrCreate(WiredGame.class);
                }

                if (game != null) {
                    game.removeHabbo(habbo);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.LEAVE_TEAM;
    }
}
