package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomBot;
import com.eu.habbo.habbohotel.units.Unit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.plugin.events.bots.BotChatEvent;
import com.eu.habbo.plugin.events.bots.BotShoutEvent;
import com.eu.habbo.plugin.events.bots.BotTalkEvent;
import com.eu.habbo.plugin.events.bots.BotWhisperEvent;
import com.eu.habbo.threading.runnables.BotFollowHabbo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Bot extends Unit implements Runnable {
    @Getter
    @Setter
    private transient int id;
    @Getter
    private String name;
    @Getter
    private String motto;
    @Getter
    private String figure;
    @Getter
    private HabboGender gender;
    @Getter
    private int ownerId;
    @Getter
    private String ownerName;
    @Getter
    @Setter
    private Room room;
    @Getter
    private boolean chatAuto;
    @Getter
    private boolean chatRandom;
    @Getter
    private short chatDelay;
    private int chatTimeOut;
    private int chatTimestamp;
    private short lastChatIndex;
    private final int bubble;
    @Getter
    private final String type;
    @Getter
    private int effect;
    private transient boolean canWalk = true;
    private boolean needsUpdate;
    private transient int followingHabboId;
    @Getter
    @Setter
    @Accessors(chain = true)
    private RoomBot roomUnit;
    public static final String NO_CHAT_SET = "${bot.skill.chatter.configuration.text.placeholder}";
    public static String[] PLACEMENT_MESSAGES = "Yo!;Hello I'm a real party animal!;Hello!".split(";");
    @Getter
    private final ArrayList<String> chatLines;

    public Bot(int id, String name, String motto, String figure, HabboGender gender, int ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.motto = motto;
        this.figure = figure;
        this.gender = gender;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.chatAuto = false;
        this.chatRandom = false;
        this.chatDelay = 1000;
        this.chatLines = new ArrayList<>();
        this.type = "generic_bot";
        this.room = null;
        this.bubble = RoomChatMessageBubbles.BOT_RENTABLE.getType();
    }

    public Bot(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.motto = set.getString("motto");
        this.figure = set.getString("figure");
        this.gender = HabboGender.valueOf(set.getString("gender"));
        this.ownerId = set.getInt(DatabaseConstants.USER_ID);
        this.ownerName = set.getString("owner_name");
        this.chatAuto = set.getString("chat_auto").equals("1");
        this.chatRandom = set.getString("chat_random").equals("1");
        this.chatDelay = set.getShort("chat_delay");
        this.chatLines = new ArrayList<>(Arrays.asList(set.getString("chat_lines").split("\r")));
        this.type = set.getString("type");
        this.effect = set.getInt("effect");
        this.canWalk = set.getString("freeroam").equals("1");
        this.room = null;
        this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.needsUpdate = false;
        this.bubble = set.getInt("bubble_id");

        this.roomUnit = new RoomBot();
    }

    public Bot(Bot bot) {
        this.name = bot.getName();
        this.motto = bot.getMotto();
        this.figure = bot.getFigure();
        this.gender = bot.getGender();
        this.ownerId = bot.getOwnerId();
        this.ownerName = bot.getOwnerName();
        this.chatAuto = true;
        this.chatRandom = false;
        this.chatDelay = 10;
        this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.chatLines = new ArrayList<>(List.of("Default Message :D"));
        this.type = bot.getType();
        this.effect = bot.getEffect();
        this.bubble = bot.getBubbleId();
        this.needsUpdate = false;
    }

    public static void initialise() {

    }

    public static void dispose() {

    }

    public void needsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean needsUpdate() {
        return this.needsUpdate;
    }

    @Override
    public void run() {
    if (this.needsUpdate) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE bots SET name = ?, motto = ?, figure = ?, gender = ?, user_id = ?, room_id = ?, rot = ?, dance = ?, freeroam = ?, chat_lines = ?, chat_auto = ?, chat_random = ?, chat_delay = ?, effect = ?, bubble_id = ? WHERE id = ?")) {
                statement.setString(1, this.name);
                statement.setString(2, this.motto);
                statement.setString(3, this.figure);
                statement.setString(4, this.gender.toString());
                statement.setInt(5, this.ownerId);
                statement.setInt(6, this.room == null ? 0 : this.room.getRoomInfo().getId());
                statement.setInt(7, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getBodyRotation().getValue());
                statement.setInt(8, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getDanceType().getType());
                statement.setString(9, this.canWalk ? "1" : "0");
                StringBuilder text = new StringBuilder();
                for (String s : this.chatLines) {
                    text.append(s).append("\r");
                }
                statement.setString(10, text.toString());
                statement.setString(11, this.chatAuto ? "1" : "0");
                statement.setString(12, this.chatRandom ? "1" : "0");
                statement.setInt(13, this.chatDelay);
                statement.setInt(14, this.effect);
                statement.setInt(15, this.bubble);
                statement.setInt(16, this.id);
                statement.execute();
                this.needsUpdate = false;
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }

    public void cycle(boolean allowBotsWalk) {
        if (this.getRoomUnit() != null) {
            if (allowBotsWalk && this.canWalk) {
                if (!this.getRoomUnit().isWalking()) {
                    if (this.getRoomUnit().getWalkTimeOut() < Emulator.getIntUnixTimestamp() && this.followingHabboId == 0) {
                        this.getRoomUnit().setGoalLocation(Emulator.getConfig().getBoolean("hotel.bot.limit.walking.distance", true) ? this.room.getLayout().getRandomWalkableTilesAround(this.getRoomUnit(), this.room.getLayout().getTile(this.getRoomUnit().getBotStartLocation().getX(), this.getRoomUnit().getBotStartLocation().getY()), this.room, Emulator.getConfig().getInt("hotel.bot.limit.walking.distance.radius", 5)) : this.room.getRandomWalkableTile());

                        int timeOut = Emulator.getRandom().nextInt(20) * 2;
                        this.getRoomUnit().setWalkTimeOut((timeOut < 10 ? 5 : timeOut) + Emulator.getIntUnixTimestamp());
                    }
                }/* else {
                    for (RoomTile t : this.room.getLayout().getTilesAround(this.room.getLayout().getTile(this.getRoomUnit().getX(), this.getRoomUnit().getY()))) {
                        WiredHandler.handle(WiredTriggerType.BOT_REACHED_STF, this.getRoomUnit(), this.room, this.room.getItemsAt(t).toArray());
                    }
                }*/
            }

            if (!this.chatLines.isEmpty() && this.chatTimeOut <= Emulator.getIntUnixTimestamp() && this.chatAuto) {
                if (this.room != null) {
                    this.lastChatIndex = (this.chatRandom ? (short) Emulator.getRandom().nextInt(this.chatLines.size()) : (this.lastChatIndex == (this.chatLines.size() - 1) ? 0 : this.lastChatIndex++));

                    if (this.lastChatIndex >= this.chatLines.size()) {
                        this.lastChatIndex = 0;
                    }

                    String message = this.chatLines.get(this.lastChatIndex)
                            .replace(Emulator.getTexts().getValue("wired.variable.owner", "%owner%"), this.room.getRoomInfo().getOwnerInfo().getUsername())
                            .replace(Emulator.getTexts().getValue("wired.variable.item_count", "%item_count%"), this.room.getRoomItemManager().getCurrentItems().size() + "")
                            .replace(Emulator.getTexts().getValue("wired.variable.name", "%name%"), this.name)
                            .replace(Emulator.getTexts().getValue("wired.variable.roomname", "%roomname%"), this.room.getRoomInfo().getName())
                            .replace(Emulator.getTexts().getValue("wired.variable.user_count", "%user_count%"), this.room.getRoomUnitManager().getRoomHabbosCount() + "");

                    if(!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.getRoomUnit(), room, new Object[]{ message })) {
                        this.talk(message);
                    }

                    this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
                }
            }
        }
    }

    public void talk(String message) {
        if (this.room != null) {
            BotChatEvent event = new BotTalkEvent(this, message);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            this.room.botChat(new ChatMessageComposer(new RoomChatMessage(event.getMessage(), this.getRoomUnit(), RoomChatMessageBubbles.getBubble(this.getBubbleId()))).compose());

            if (message.equals("o/") || message.equals("_o/")) {
                this.room.sendComposer(new ExpressionMessageComposer(this.getRoomUnit(), RoomUserAction.WAVE).compose());
            }
        }
    }

    public void shout(String message) {
        if (this.room != null) {
            BotChatEvent event = new BotShoutEvent(this, message);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            this.room.botChat(new ShoutMessageComposer(new RoomChatMessage(event.getMessage(), this.getRoomUnit(), RoomChatMessageBubbles.getBubble(this.getBubbleId()))).compose());

            if (message.equals("o/") || message.equals("_o/")) {
                this.room.sendComposer(new ExpressionMessageComposer(this.getRoomUnit(), RoomUserAction.WAVE).compose());
            }
        }
    }

    public void whisper(String message, Habbo habbo) {
        if (this.room != null && habbo != null) {
            BotWhisperEvent event = new BotWhisperEvent(this, message, habbo);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            event.getTarget().getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(event.getMessage(), this.getRoomUnit(), RoomChatMessageBubbles.getBubble(this.getBubbleId()))));
        }
    }

    public void onPlace(Habbo habbo, Room room) {
        if (this.getRoomUnit() != null) {
            this.getRoomUnit().giveEffect(this.effect, -1);
        }

        if(PLACEMENT_MESSAGES.length > 0) {
            String message = PLACEMENT_MESSAGES[Emulator.getRandom().nextInt(PLACEMENT_MESSAGES.length)];
            if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.getRoomUnit(), room, new Object[]{message})) {
                this.talk(message);
            }
        }
    }

    public void onPickUp(Habbo habbo, Room room) {

    }

    public void onUserSay(final RoomChatMessage message) {

    }

    public int getBubbleId() {
        return bubble;
    }

    public void setName(String name) {
        this.name = name;
        this.needsUpdate = true;

        //if(this.room != null)
        //this.room.sendComposer(new ChangeNameUpdatedComposer(this.getRoomUnit(), this.getName()).compose());
    }

    public void setMotto(String motto) {
        this.motto = motto;
        this.needsUpdate = true;
    }

    public void setFigure(String figure) {
        this.figure = figure;
        this.needsUpdate = true;

        if (this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setGender(HabboGender gender) {
        this.gender = gender;
        this.needsUpdate = true;

        if (this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
        this.needsUpdate = true;

        if (this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        this.needsUpdate = true;

        if (this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setChatAuto(boolean chatAuto) {
        this.chatAuto = chatAuto;
        this.needsUpdate = true;
    }

    public void setChatRandom(boolean chatRandom) {
        this.chatRandom = chatRandom;
        this.needsUpdate = true;
    }

    public boolean hasChat() {
        return !this.chatLines.isEmpty();
    }

    public void setChatDelay(short chatDelay) {
        this.chatDelay = (short) Math.min(Math.max(chatDelay, BotManager.MINIMUM_CHAT_SPEED), BotManager.MAXIMUM_CHAT_SPEED);
        this.needsUpdate = true;
        this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
    }

    public int getChatTimestamp() {
        return this.chatTimestamp;
    }

    public void clearChat() {
        synchronized (this.chatLines) {
            this.chatLines.clear();
            this.needsUpdate = true;
        }
    }

    public void setEffect(int effect, int duration) {
        this.effect = effect;
        this.needsUpdate = true;

        if (this.getRoomUnit() != null) {
            if (this.room != null) {
                this.getRoomUnit().giveEffect(this.effect, duration);
            }
        }
    }

    public void addChatLines(ArrayList<String> chatLines) {
        synchronized (this.chatLines) {
            this.chatLines.addAll(chatLines);
            this.needsUpdate = true;
        }
    }

    public void addChatLine(String chatLine) {
        synchronized (this.chatLines) {
            this.chatLines.add(chatLine);
            this.needsUpdate = true;
        }
    }

    public int getFollowingHabboId() {
        return this.followingHabboId;
    }

    public void startFollowingHabbo(Habbo habbo) {
        this.followingHabboId = habbo.getHabboInfo().getId();

        Emulator.getThreading().run(new BotFollowHabbo(this, habbo, habbo.getRoomUnit().getRoom()));
    }

    public void stopFollowingHabbo() {
        this.followingHabboId = 0;
    }

    public boolean canWalk() {
        return this.canWalk;
    }

    public void setCanWalk(boolean canWalk) {
        this.canWalk = canWalk;
    }

    public void lookAt(Habbo habbo) {
        this.lookAt(habbo.getRoomUnit().getCurrentPosition());
    }

    public void lookAt(RoomUnit roomUnit) {
        this.lookAt(roomUnit.getCurrentPosition());
    }

    public void lookAt(RoomTile tile) {
        this.getRoomUnit().lookAtPoint(tile);
        this.getRoomUnit().setStatusUpdateNeeded(true);
    }

    public void onPlaceUpdate() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE bots SET name = ?, motto = ?, figure = ?, gender = ?, user_id = ?, room_id = ?, x = ?, y = ?, z = ?, rot = ?, dance = ?, freeroam = ?, chat_lines = ?, chat_auto = ?, chat_random = ?, chat_delay = ?, effect = ?, bubble_id = ? WHERE id = ?")) {
            statement.setString(1, this.name);
            statement.setString(2, this.motto);
            statement.setString(3, this.figure);
            statement.setString(4, this.gender.toString());
            statement.setInt(5, this.ownerId);
            statement.setInt(6, this.room == null ? 0 : this.room.getRoomInfo().getId());
            statement.setInt(7, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getCurrentPosition().getX());
            statement.setInt(8, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getCurrentPosition().getY());
            statement.setDouble(9, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getCurrentZ());
            statement.setInt(10, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getBodyRotation().getValue());
            statement.setInt(11, this.getRoomUnit() == null ? 0 : this.getRoomUnit().getDanceType().getType());
            statement.setString(12, this.canWalk ? "1" : "0");
            StringBuilder text = new StringBuilder();
            for (String s : this.chatLines) {
                text.append(s).append("\r");
            }
            statement.setString(13, text.toString());
            statement.setString(14, this.chatAuto ? "1" : "0");
            statement.setString(15, this.chatRandom ? "1" : "0");
            statement.setInt(16, this.chatDelay);
            statement.setInt(17, this.effect);
            statement.setInt(18, this.bubble);
            statement.setInt(19, this.id);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

}
