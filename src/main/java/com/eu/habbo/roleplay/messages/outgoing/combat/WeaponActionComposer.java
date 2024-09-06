package com.eu.habbo.roleplay.messages.outgoing.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.users.HabboWeapon;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WeaponActionComposer extends MessageComposer {

    private final HabboWeapon weapon;
    private final Habbo currentUser;

    @Override
    protected ServerMessage composeInternal() {
        Habbo weaponUser = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.weapon.getUserID());

        if (weaponUser.getRoomUnit().getRoom().getRoomInfo().getId() != this.currentUser.getRoomUnit().getRoom().getRoomInfo().getId()) {
            return null;
        }

        RoomTile weaponCoords = weaponUser.getRoomUnit().getCurrentPosition();
        RoomTile userCoords = this.currentUser.getRoomUnit().getCurrentPosition();

        double maxDistance = this.weapon.getWeapon().getRangeInTiles() + 2;
        double distance = Math.sqrt(Math.pow((weaponCoords.getX() - userCoords.getX()), 2) + Math.pow((weaponCoords.getY() - userCoords.getY()), 2));

        this.response.init(Outgoing.weaponActionComposer);
        this.response.appendString(this.weapon.getWeapon().getUniqueName());
        this.response.appendDouble(this.adjustVolumeBasedOnDistance(distance, maxDistance));
        return this.response;
    }

    double adjustVolumeBasedOnDistance(double distance, double maxDistance) {
        if (distance >= maxDistance) {
            return 0.0;
        }
        return Math.max(0, 1 - (distance / maxDistance));
    }

}
