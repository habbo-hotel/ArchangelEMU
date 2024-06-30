package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;
import com.eu.habbo.roleplay.interactions.InteractionFish;
import com.eu.habbo.roleplay.interactions.InteractionFishingPole;
import gnu.trove.set.hash.THashSet;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class FishingAction implements Runnable {

    private static final Random random = new Random();

    public static int XP_PER_CYCLE = 4;
    public static int FISHING_HAND_ITEM_BAIT = 1092;
    public static int FISHING_HAND_ITEM_CAUGHT = 1090;

    private final Habbo habbo;
    private final RoomItem roomItem;
    private final RoomTile roomTile;

    private int fishingCycles = 0;

    @Override
    public void run() {
        if (this.habbo == null) {
            return;
        }

        THashSet<RoomItem> ownedFishingPoles = this.habbo.getInventory().getItemsComponent().getItemsByInteractionType(InteractionFishingPole.class);

        if (ownedFishingPoles.isEmpty()) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.no_pole"));
            return;
        }

        if (this.habbo.getRoomUnit().getRoom() == null) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.cancel"));
            return;
        }

        if (this.roomItem.getRoom() == null) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.cancel"));
            return;
        }

        if (this.habbo.getRoomUnit().getRoom().getRoomInfo().getId() != this.roomItem.getRoom().getRoomInfo().getId()) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.cancel"));
            return;
        }

        if (this.habbo.getRoomUnit().getLastRoomTile() != this.roomTile) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.cancel"));
            return;
        }

        if (this.fishingCycles == 0) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.start"));
            this.habbo.getRoomUnit().setHandItem(FishingAction.FISHING_HAND_ITEM_BAIT);
            this.habbo.getRoomUnit().getRoom().sendComposer(new CarryObjectMessageComposer(this.habbo.getRoomUnit()).compose());
        }

        if (this.fishingCycles > 1) {
            double baseRatio = Emulator.getConfig().getDouble("roleplay.fishing.catch_ratio", 0.3);
            double catchRatio = baseRatio + (this.habbo.getHabboRoleplayStats().getFishingLevel().getCurrentLevel() * baseRatio);
            boolean didCatchFish = random.nextDouble() < catchRatio;
            if (didCatchFish) {
                this.onFishingComplete();
                return;
            }
        }
        this.fishingCycles += 1;


        this.habbo.getHabboRoleplayStats().addFishingXP(FishingAction.XP_PER_CYCLE);

        Emulator.getThreading().run(this, 1000);
    }

    public void onFishingComplete() {
        this.habbo.getHabboInfo().run();
        this.habbo.shout(Emulator.getTexts().getValue("roleplay.fishing.success"));
        this.habbo.getRoomUnit().setHandItem(FishingAction.FISHING_HAND_ITEM_CAUGHT);
        this.habbo.getRoomUnit().getRoom().sendComposer(new CarryObjectMessageComposer(this.habbo.getRoomUnit()).compose());


        Emulator.getThreading().run(() -> {
            this.habbo.getRoomUnit().setHandItem(0);
            this.habbo.getRoomUnit().getRoom().sendComposer(new CarryObjectMessageComposer(this.habbo.getRoomUnit()).compose());
        }, 2000);

        Item fishBaseItem = Emulator.getGameEnvironment().getItemManager().getItemByInteractionType(InteractionFish.class);
        RoomItem fishRoomItem = Emulator.getGameEnvironment().getItemManager().createItem(this.habbo.getHabboInfo().getId(), fishBaseItem, 0, 0, "");
        this.habbo.getInventory().getItemsComponent().addItem(fishRoomItem);
        this.habbo.getClient().sendResponse(new UnseenItemsComposer(fishRoomItem));
        this.habbo.getClient().sendResponse(new FurniListInvalidateComposer());
    }

}
