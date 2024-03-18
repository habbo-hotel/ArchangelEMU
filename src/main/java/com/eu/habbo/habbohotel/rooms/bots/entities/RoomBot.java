package com.eu.habbo.habbohotel.rooms.bots.entities;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.chat.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomUserAction;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.ExpressionMessageComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.bots.BotTalkEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomBot extends RoomAvatar {
    private Bot unit;

    private RoomTile spawnTile;
    private double spawnHeight;

    public RoomBot() {
        super();
    }

    @Override
    public void cycle() {
        if (this.room.isAllowBotsWalk() && this.isCanWalk()) {
            if (!this.isWalking()) {
                if (this.getWalkTimeOut() < Emulator.getIntUnixTimestamp() && this.unit.getFollowingHabboId() == 0) {
                    this.walkTo(Emulator.getConfig().getBoolean("hotel.bot.limit.walking.distance", true) ? this.getRoom().getLayout().getRandomWalkableTilesAround(this, this.currentPosition, Emulator.getConfig().getInt("hotel.bot.limit.walking.distance.radius", 5)) : this.getRoom().getRandomWalkableTile());
                    int timeOut = Emulator.getRandom().nextInt(20) * 2;
                    this.setWalkTimeOut((timeOut < 10 ? 5 : timeOut) + Emulator.getIntUnixTimestamp());
                }
            }
        }

        boolean hasChatLines = !this.unit.getChatLines().isEmpty();
        boolean hasAutoChatEnabled = this.unit.isChatAuto();
        boolean chatTimeOutPassed = Emulator.getIntUnixTimestamp() >= this.unit.getChatTimeOut();

        if (hasChatLines && chatTimeOutPassed && hasAutoChatEnabled) {
            if(this.unit.isChatRandom()) {
                this.unit.setLastChatIndex((short) Emulator.getRandom().nextInt(this.unit.getChatLines().size()));
            } else if(this.unit.getLastChatIndex() == this.unit.getChatLines().size() - 1) {
                this.unit.resetLastChatIndex();
            } else {
                this.unit.incrementLastChatIndex();
            }

            if (this.unit.getLastChatIndex() >= this.unit.getChatLines().size()) {
                this.unit.resetLastChatIndex();
            }

            String message = this.unit.getChatLines().get(this.unit.getLastChatIndex())
                    .replace(Emulator.getTexts().getValue("wired.variable.owner", "%owner%"), this.getRoom().getRoomInfo().getOwnerInfo().getUsername())
                    .replace(Emulator.getTexts().getValue("wired.variable.item_count", "%item_count%"), String.valueOf(this.getRoom().getRoomItemManager().getCurrentItems().size()))
                    .replace(Emulator.getTexts().getValue("wired.variable.name", "%name%"), this.unit.getName())
                    .replace(Emulator.getTexts().getValue("wired.variable.roomname", "%roomname%"), this.getRoom().getRoomInfo().getName())
                    .replace(Emulator.getTexts().getValue("wired.variable.user_count", "%user_count%"), String.valueOf(this.getRoom().getRoomUnitManager().getRoomHabbosCount()));

            if(!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this, room, new Object[]{ message })) {
                this.talk(message);
            }

            this.unit.setChatTimeOut(Emulator.getIntUnixTimestamp() + this.unit.getChatDelay());
        }

        super.cycle();
    }

    public void talk(String message) {
        if (Emulator.getPluginManager().isRegistered(BotTalkEvent.class, false)) {
            Event event = new BotTalkEvent(this.unit, message);
            Emulator.getPluginManager().fireEvent(event);

            if (event.isCancelled())
                return;
        }

        this.unit.setChatTimestamp(Emulator.getIntUnixTimestamp());
        this.room.botChat(new ChatMessageComposer(new RoomChatMessage(message, this, RoomChatMessageBubbles.getBubble(this.unit.getBubbleId()))).compose());

        if (message.equals("o/") || message.equals("_o/")) {
            this.room.sendComposer(new ExpressionMessageComposer(this, RoomUserAction.WAVE).compose());
        }
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.BOT;
    }
}
