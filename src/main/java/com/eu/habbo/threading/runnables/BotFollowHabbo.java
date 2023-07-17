package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BotFollowHabbo implements Runnable {
    private final Bot bot;
    private final Habbo habbo;
    private final Room room;
    private boolean hasReached;

    @Override
    public void run() {
        if (this.bot != null) {
            if (this.habbo != null && this.bot.getFollowingHabboId() == this.habbo.getHabboInfo().getId()) {
                if (this.habbo.getRoomUnit().getRoom() != null && this.habbo.getRoomUnit().getRoom() == this.room) {
                    if (this.habbo.getRoomUnit() != null) {
                        if (this.bot.getRoomUnit() != null) {
                            RoomTile target = this.room.getLayout().getTileInFront(this.habbo.getRoomUnit().getCurrentPosition(), Math.abs((this.habbo.getRoomUnit().getBodyRotation().getValue() + 4)) % 8);

                            if (target != null) {
                                if (target.getX() < 0 || target.getY() < 0)
                                    target = this.room.getLayout().getTileInFront(this.habbo.getRoomUnit().getCurrentPosition(), this.habbo.getRoomUnit().getBodyRotation().getValue());

                                if(this.habbo.getRoomUnit().getCurrentPosition().distance(this.bot.getRoomUnit().getCurrentPosition()) < 2) {
                                    if(!hasReached) {
                                        WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, bot.getRoomUnit(), room, new Object[]{});
                                        hasReached = true;
                                    }
                                }
                                else {
                                    hasReached = false;
                                }

                                if (target.getX() >= 0 && target.getY() >= 0) {
                                    this.bot.getRoomUnit().setGoalLocation(target);
                                    this.bot.getRoomUnit().setCanWalk(true);
                                    Emulator.getThreading().run(this, 500);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
