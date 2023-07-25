package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import lombok.Getter;

@Getter
public class RoomBot extends RoomAvatar {
    public RoomBot() {
        super();
    }

    @Override
    public boolean cycle(Room room) {
        Bot bot = this.getRoom().getRoomUnitManager().getBotByRoomUnit(this);

        if(bot == null) {
            return false;
        }

        if (this.getRoom().isAllowBotsWalk()) {
            if(bot.canWalk()) {
                if (!this.isWalking()) {
                    if (this.getWalkTimeOut() < Emulator.getIntUnixTimestamp() && bot.getFollowingHabboId() == 0) {
                        this.setGoalLocation(Emulator.getConfig().getBoolean("hotel.bot.limit.walking.distance", true) ? this.getRoom().getLayout().getRandomWalkableTilesAround(this, this.getRoom().getLayout().getTile(this.getBotStartLocation().getX(), this.getBotStartLocation().getY()), this.getRoom(), Emulator.getConfig().getInt("hotel.bot.limit.walking.distance.radius", 5)) : this.getRoom().getRandomWalkableTile());
                        int timeOut = Emulator.getRandom().nextInt(20) * 2;
                        this.setWalkTimeOut((timeOut < 10 ? 5 : timeOut) + Emulator.getIntUnixTimestamp());
                    }
                }
            }
        }

        if (!bot.getChatLines().isEmpty() && bot.getChatTimeOut() <= Emulator.getIntUnixTimestamp() && bot.isChatAuto()) {
            if (this.getRoom() != null) {
                short test = 0;

                if(bot.isChatRandom()) {
                    bot.setLastChatIndex((short) Emulator.getRandom().nextInt(bot.getChatLines().size()));
                } else if(bot.getLastChatIndex() == bot.getChatLines().size() - 1) {
                    bot.resetLastChatIndex();
                } else {
                    bot.incrementLastChatIndex();
                }

                if (bot.getLastChatIndex() >= bot.getChatLines().size()) {
                    bot.resetLastChatIndex();
                }

                String message = bot.getChatLines().get(bot.getLastChatIndex())
                        .replace(Emulator.getTexts().getValue("wired.variable.owner", "%owner%"), this.getRoom().getRoomInfo().getOwnerInfo().getUsername())
                        .replace(Emulator.getTexts().getValue("wired.variable.item_count", "%item_count%"), String.valueOf(this.getRoom().getRoomItemManager().getCurrentItems().size()))
                        .replace(Emulator.getTexts().getValue("wired.variable.name", "%name%"), bot.getName())
                        .replace(Emulator.getTexts().getValue("wired.variable.roomname", "%roomname%"), this.getRoom().getRoomInfo().getName())
                        .replace(Emulator.getTexts().getValue("wired.variable.user_count", "%user_count%"), String.valueOf(this.getRoom().getRoomUnitManager().getRoomHabbosCount()));

                if(!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this, room, new Object[]{ message })) {
                    bot.talk(message);
                }

                bot.setChatTimeOut(Emulator.getIntUnixTimestamp() + bot.getChatDelay());
            }
        }

        return super.cycle(room);
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.BOT;
    }
}
