package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.HabboLicenseRepository;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.interactions.InteractionToolPickaxe;
import gnu.trove.set.hash.THashSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MiningAction implements Runnable {
    private final int startedAt;
    private final Habbo habbo;
    private final RoomItem roomItem;
    private final RoomTile roomTile;

    private int oreGained = 0;

    @Override
    public void run() {
        if (this.habbo == null) {
            this.stopMining();
            return;
        }

        THashSet<RoomItem> ownedPickaxes = this.habbo.getInventory().getItemsComponent().getItemsByInteractionType(InteractionToolPickaxe.class);

        if (ownedPickaxes.isEmpty()) {
            this.stopMining();
            return;
        }

        if (this.habbo.getRoomUnit().getRoom() == null) {
            this.stopMining();
            return;
        }

        if (this.roomItem.getRoom() == null) {
            this.stopMining();
            return;
        }

        if (this.habbo.getRoomUnit().getRoom().getRoomInfo().getId() != this.roomItem.getRoom().getRoomInfo().getId()) {
            this.stopMining();
            return;
        }

        if (this.habbo.getRoomUnit().getLastRoomTile() != this.roomTile) {
            this.stopMining();
            return;
        }


        this.oreGained += 10;

        this.habbo.shout("i gained 10 ore and am gonna keep hitting");
        Emulator.getThreading().run(this, 1000);
    }

    public void stopMining() {
        this.habbo.shout("i stopped mining");
    }

}
