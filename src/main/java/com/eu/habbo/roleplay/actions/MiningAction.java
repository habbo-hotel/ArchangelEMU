package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.roleplay.interactions.InteractionToolPickaxe;
import gnu.trove.set.hash.THashSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MiningAction implements Runnable {

    public static int DAMAGE_PER_STRIKE = 1;
    public static int HEALTH_PER_BLOCK = 10;
    public static int MINING_EFFECT_ID = 116;

    private final Habbo habbo;
    private final RoomItem roomItem;
    private final RoomTile roomTile;

    private int totalDamage = 0;

    @Override
    public void run() {
        if (this.habbo == null) {
            return;
        }

        THashSet<RoomItem> ownedPickaxes = this.habbo.getInventory().getItemsComponent().getItemsByInteractionType(InteractionToolPickaxe.class);

        if (ownedPickaxes.isEmpty()) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.no_pickaxe"));
            return;
        }

        if (this.habbo.getRoomUnit().getRoom() == null) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.cancel"));
            return;
        }

        if (this.roomItem.getRoom() == null) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.cancel"));
            return;
        }

        if (this.habbo.getRoomUnit().getRoom().getRoomInfo().getId() != this.roomItem.getRoom().getRoomInfo().getId()) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.cancel"));
            return;
        }

        if (this.habbo.getRoomUnit().getLastRoomTile() != this.roomTile) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.cancel"));
            return;
        }

        if (this.totalDamage == 0) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.start"));
            this.habbo.getRoomUnit().giveEffect(MiningAction.MINING_EFFECT_ID, -1);
        }

        int damageMade = MiningAction.DAMAGE_PER_STRIKE + this.habbo.getHabboRoleplayStats().getMiningLevel().getCurrentLevel() + this.habbo.getHabboRoleplayStats().getStrengthLevel().getCurrentLevel();
        this.totalDamage += damageMade;

        if (this.totalDamage >= MiningAction.HEALTH_PER_BLOCK) {
            this.onMiningComplete();
            return;
        }

        this.habbo.getHabboRoleplayStats().addStrengthXP(damageMade);
        this.habbo.getHabboRoleplayStats().addMiningXP(damageMade);

        this.habbo.shout(Emulator.getTexts()
                .getValue("roleplay.mining.progress")
                .replace(":damage", String.valueOf(totalDamage))
                .replace(":health", String.valueOf(MiningAction.HEALTH_PER_BLOCK))
        );
        Emulator.getThreading().run(this, 1000);
    }

    public void onMiningComplete() {
        this.habbo.getHabboInfo().run();
        this.habbo.shout(Emulator.getTexts().getValue("roleplay.mining.success"));
        this.habbo.getRoomUnit().giveEffect(0, -1);

        RoomItem item = Emulator.getGameEnvironment().getItemManager().createItem(this.habbo.getHabboInfo().getId(), this.roomItem.getBaseItem(), 0, 0, "");
        this.habbo.getInventory().getItemsComponent().addItem(item);
        this.habbo.getClient().sendResponse(new UnseenItemsComposer(item));
        this.habbo.getClient().sendResponse(new FurniListInvalidateComposer());

        new RespawnItemAction(this.roomItem);
    }

}
