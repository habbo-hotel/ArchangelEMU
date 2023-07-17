package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.HandItemReceivedMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboGiveHandItemToHabbo implements Runnable {
    private final Habbo target;
    private final Habbo from;

    @Override
    public void run() {
        if (this.from.getRoomUnit().getRoom() == null || this.target.getRoomUnit().getRoom() == null)
            return;

        if (this.from.getRoomUnit().getRoom() != this.target.getRoomUnit().getRoom())
            return;

        int itemId = this.from.getRoomUnit().getHandItem();

        if (itemId > 0) {
            this.from.getRoomUnit().setHandItem(0);
            this.from.getRoomUnit().getRoom().sendComposer(new CarryObjectMessageComposer(this.from.getRoomUnit()).compose());
            this.target.getRoomUnit().lookAtPoint(this.from.getRoomUnit().getCurrentPosition());
            this.target.getRoomUnit().setStatusUpdateNeeded(true);
            this.target.getClient().sendResponse(new HandItemReceivedMessageComposer(this.from.getRoomUnit(), itemId));
            this.target.getRoomUnit().setHandItem(itemId);
            this.target.getRoomUnit().getRoom().sendComposer(new CarryObjectMessageComposer(this.target.getRoomUnit()).compose());
        }
    }
}
