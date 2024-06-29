package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.interactions.InteractionToolPickaxe;
import gnu.trove.set.hash.THashSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MiningAction implements Runnable {

    public static int ORE_PER_STRIKE = 1;
    public static int ORE_PER_BLOCK = 10;
    public static int MINING_EFFECT_ID = 116;

    private final Habbo habbo;
    private final String oldMotto;
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

        this.oreGained += MiningAction.ORE_PER_STRIKE;

        if (this.oreGained == MiningAction.ORE_PER_STRIKE) {
            this.habbo.shout("*starts mining*");
            this.habbo.getRoomUnit().giveEffect(MiningAction.MINING_EFFECT_ID, -1);
        }

        if (this.oreGained >= MiningAction.ORE_PER_BLOCK) {
            this.stopMining();
            new MoveOreAction(this.roomItem.getRoom().getRoomUnitManager().getRoomUnitsAt(this.roomTile));
            this.habbo.getHabboInfo().setMotto(this.oldMotto);
            this.habbo.getHabboInfo().run();
            this.habbo.shout("*destroys the ore and gains a block*");
            this.habbo.getRoomUnit().giveEffect(0, -1);
            return;
        }

        this.habbo.getHabboInfo().setMotto("mined" + oreGained + "/" + ORE_PER_BLOCK + "  ores");
        this.habbo.shout("i gained 10 ore and am gonna keep hitting");
        Emulator.getThreading().run(this, 1000);
    }

    public void stopMining() {
        this.habbo.shout("i stopped mining");
    }

}
