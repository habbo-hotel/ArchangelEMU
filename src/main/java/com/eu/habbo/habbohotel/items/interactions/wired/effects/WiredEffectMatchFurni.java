package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class WiredEffectMatchFurni extends InteractionWiredEffect implements InteractionWiredMatchFurniSettings {
    private final boolean checkForWiredResetPermission = true;
    public final int PARAM_STATE = 0;
    public final int PARAM_ROTATION = 1;
    public final int PARAM_POSITION = 2;
    @Getter
    private THashSet<WiredMatchFurniSetting> matchFurniSettings;

    public WiredEffectMatchFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMatchFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        boolean state = this.getWiredSettings().getIntegerParams().get(PARAM_STATE) == 1;
        boolean position = this.getWiredSettings().getIntegerParams().get(PARAM_POSITION) == 1;
        boolean rotation = this.getWiredSettings().getIntegerParams().get(PARAM_ROTATION) == 1;

        for(HabboItem item : this.getWiredSettings().getItems(room)) {
            WiredMatchFurniSetting setting = new WiredMatchFurniSetting(item.getId(), item.getExtradata(), item.getRotation(), item.getX(), item.getY());

            this.matchFurniSettings.add(setting);

            if (state && (this.checkForWiredResetPermission && item.allowWiredResetState())) {
                if (!setting.getState().equals(" ") && !item.getExtradata().equals(setting.getState())) {
                    item.setExtradata(setting.getState());
                    room.updateItemState(item);
                }
            }

            RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
            double oldZ = item.getZ();

            if(rotation && !position) {
                if(item.getRotation() != setting.getRotation() && room.furnitureFitsAt(oldLocation, item, setting.getRotation(), false) == FurnitureMovementError.NONE) {
                    room.moveFurniTo(item, oldLocation, setting.getRotation(), null, true);
                }
            }
            else if(position) {
                boolean slideAnimation = !rotation || item.getRotation() == setting.getRotation();
                RoomTile newLocation = room.getLayout().getTile((short) setting.getX(), (short) setting.getY());
                int newRotation = rotation ? setting.getRotation() : item.getRotation();

                if(newLocation != null && newLocation.getState() != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != item.getRotation()) && room.furnitureFitsAt(newLocation, item, newRotation, true) == FurnitureMovementError.NONE) {
                    if(room.moveFurniTo(item, newLocation, newRotation, null, !slideAnimation) == FurnitureMovementError.NONE) {
                        if(slideAnimation) {
                            room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getZ(), 0, room).compose());
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean shouldMatchState() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_STATE) == 1;
    }

    @Override
    public boolean shouldMatchRotation() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_ROTATION) == 1;
    }

    @Override
    public boolean shouldMatchPosition() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_POSITION) == 1;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.MATCH_SSHOT;
    }
}
