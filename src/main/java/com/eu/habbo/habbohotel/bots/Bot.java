package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.constants.RoomUserAction;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomBot;
import com.eu.habbo.habbohotel.units.type.Avatar;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.plugin.events.bots.BotChatEvent;
import com.eu.habbo.plugin.events.bots.BotShoutEvent;
import com.eu.habbo.plugin.events.bots.BotTalkEvent;
import com.eu.habbo.plugin.events.bots.BotWhisperEvent;
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

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class Bot extends Avatar implements Runnable {
    private transient int id;
    private String name;
    private String motto;
    private String figure;
    private HabboGender gender;
    private int ownerId;
    private String ownerName;

    private HabboInfo ownerInfo;

    private boolean chatAuto;
    private boolean chatRandom;
    private short chatDelay;
    private int chatTimeOut;
    private int chatTimestamp;
    private short lastChatIndex;
    private final int bubbleId;
    private final String type;
    private int effect;
    private boolean sqlUpdateNeeded;
    private transient int followingHabboId;
    protected final RoomBot roomUnit;
    public static final String NO_CHAT_SET = "${bot.skill.chatter.configuration.text.placeholder}";
    public static String[] PLACEMENT_MESSAGES = "Yo!;Hello I'm a real party animal!;Hello!".split(";");
    private final ArrayList<String> chatLines;

    public Bot(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.motto = set.getString("motto");
        this.figure = set.getString("figure");
        this.gender = HabboGender.valueOf(set.getString("gender"));

        //@Deprecated
        this.ownerId = set.getInt("owner_id");
        this.ownerName = set.getString("owner_name");

        this.ownerInfo = Emulator.getGameEnvironment().getHabboManager().getOfflineHabboInfo(set.getInt("owner_id"));

        this.chatAuto = set.getString("chat_auto").equals("1");
        this.chatRandom = set.getString("chat_random").equals("1");
        this.chatDelay = set.getShort("chat_delay");
        this.chatLines = new ArrayList<>(Arrays.asList(set.getString("chat_lines").split("\r")));
        this.type = set.getString("type");
        this.effect = set.getInt("effect");
        this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.sqlUpdateNeeded = false;
        this.bubbleId = set.getInt("bubble_id");

        this.roomUnit = new RoomBot();
        this.roomUnit.setUnit(this);

        this.roomUnit.setCanWalk(set.getString("freeroam").equals("1"));
    }

    public static void initialise() {}

    public static void dispose() {}

    @Override
    public void run() {
        if (this.sqlUpdateNeeded) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE bots SET name = ?, motto = ?, figure = ?, gender = ?, owner_id = ?, room_id = ?, rot = ?, dance = ?, freeroam = ?, chat_lines = ?, chat_auto = ?, chat_random = ?, chat_delay = ?, effect = ?, bubble_id = ?, x = ?, y = ?, z = ? WHERE id = ?")) {
                statement.setString(1, this.name);
                statement.setString(2, this.motto);
                statement.setString(3, this.figure);
                statement.setString(4, this.gender.toString());
                statement.setInt(5, this.ownerInfo.getId());
                statement.setInt(6, this.roomUnit.getRoom() == null ? 0 : this.roomUnit.getRoom().getRoomInfo().getId());
                statement.setInt(7, this.roomUnit.getBodyRotation().getValue());
                statement.setInt(8, this.roomUnit.getDanceType().getType());
                statement.setString(9, this.roomUnit.isCanWalk() ? "1" : "0");
                StringBuilder text = new StringBuilder();
                for (String s : this.chatLines) {
                    text.append(s).append("\r");
                }
                statement.setString(10, text.toString());
                statement.setString(11, this.chatAuto ? "1" : "0");
                statement.setString(12, this.chatRandom ? "1" : "0");
                statement.setInt(13, this.chatDelay);
                statement.setInt(14, this.effect);
                statement.setInt(15, this.bubbleId);
                statement.setInt(16, this.roomUnit.getSpawnTile() == null ? 0 : this.roomUnit.getSpawnTile().getX());
                statement.setInt(17, this.roomUnit.getSpawnTile() == null ? 0 : this.roomUnit.getSpawnTile().getY());
                statement.setDouble(18, this.roomUnit.getSpawnHeight());
                statement.setInt(19, this.id);
                statement.execute();
                this.sqlUpdateNeeded = false;
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    public void talk(String message) {
        if (this.roomUnit.getRoom() != null) {
            BotChatEvent event = new BotTalkEvent(this, message);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            this.roomUnit.getRoom().botChat(new ChatMessageComposer(new RoomChatMessage(event.getMessage(), this.roomUnit, RoomChatMessageBubbles.getBubble(this.getBubbleId()))).compose());

            if (message.equals("o/") || message.equals("_o/")) {
                this.roomUnit.getRoom().sendComposer(new ExpressionMessageComposer(this.roomUnit, RoomUserAction.WAVE).compose());
            }
        }
    }

    public void shout(String message) {
        if (this.roomUnit.getRoom() != null) {
            BotChatEvent event = new BotShoutEvent(this, message);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            this.roomUnit.getRoom().botChat(new ShoutMessageComposer(new RoomChatMessage(event.getMessage(), this.roomUnit, RoomChatMessageBubbles.getBubble(this.getBubbleId()))).compose());

            if (message.equals("o/") || message.equals("_o/")) {
                this.roomUnit.getRoom().sendComposer(new ExpressionMessageComposer(this.roomUnit, RoomUserAction.WAVE).compose());
            }
        }
    }

    public void whisper(String message, Habbo habbo) {
        if (this.roomUnit.getRoom() != null && habbo != null) {
            BotWhisperEvent event = new BotWhisperEvent(this, message, habbo);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.chatTimestamp = Emulator.getIntUnixTimestamp();
            event.getTarget().getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(event.getMessage(), this.roomUnit, RoomChatMessageBubbles.getBubble(this.getBubbleId()))));
        }
    }

    public void onPlace(Habbo habbo, Room room) {
        if (this.roomUnit != null) {
            this.roomUnit.giveEffect(this.effect, -1);
        }

        if(PLACEMENT_MESSAGES.length > 0) {
            String message = PLACEMENT_MESSAGES[Emulator.getRandom().nextInt(PLACEMENT_MESSAGES.length)];
            if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.roomUnit, room, new Object[]{message})) {
                this.talk(message);
            }
        }
    }

    public void onPickUp(Habbo habbo, Room room) {}

    public void onUserSay(final RoomChatMessage message) {}

    public void setName(String name) {
        this.name = name;
        this.sqlUpdateNeeded = true;

        //if(this.room != null)
        //this.roomUnit.getRoom().sendComposer(new ChangeNameUpdatedComposer(this.roomUnit, this.getName()).compose());
    }

    public void setMotto(String motto) {
        this.motto = motto;
        this.sqlUpdateNeeded = true;
    }

    public void setFigure(String figure) {
        this.figure = figure;
        this.sqlUpdateNeeded = true;
        this.roomUnit.getRoom().sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setGender(HabboGender gender) {
        this.gender = gender;
        this.sqlUpdateNeeded = true;
        this.roomUnit.getRoom().sendComposer(new RoomUsersComposer(this).compose());
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
        this.sqlUpdateNeeded = true;

        if(this.roomUnit.getRoom() != null) {
            this.roomUnit.getRoom().sendComposer(new RoomUsersComposer(this).compose());
        }
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        this.sqlUpdateNeeded = true;

        if(this.roomUnit.getRoom() != null) {
            this.roomUnit.getRoom().sendComposer(new RoomUsersComposer(this).compose());
        }
    }

    public void setChatAuto(boolean chatAuto) {
        this.chatAuto = chatAuto;
        this.sqlUpdateNeeded = true;
    }

    public void setChatRandom(boolean chatRandom) {
        this.chatRandom = chatRandom;
        this.sqlUpdateNeeded = true;
    }

    public boolean hasChat() {
        return !this.chatLines.isEmpty();
    }

    public void setChatDelay(short chatDelay) {
        this.chatDelay = (short) Math.min(Math.max(chatDelay, BotManager.MINIMUM_CHAT_SPEED), BotManager.MAXIMUM_CHAT_SPEED);
        this.sqlUpdateNeeded = true;
        this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
    }

    public void clearChat() {
        synchronized (this.chatLines) {
            this.chatLines.clear();
            this.sqlUpdateNeeded = true;
        }
    }

    public void setEffect(int effect, int duration) {
        this.effect = effect;
        this.sqlUpdateNeeded = true;

        if (this.roomUnit.getRoom() != null) {
            this.roomUnit.giveEffect(this.effect, duration);
        }
    }

    public void addChatLines(ArrayList<String> chatLines) {
        synchronized (this.chatLines) {
            this.chatLines.addAll(chatLines);
            this.sqlUpdateNeeded = true;
        }
    }

    public void addChatLine(String chatLine) {
        synchronized (this.chatLines) {
            this.chatLines.add(chatLine);
            this.sqlUpdateNeeded = true;
        }
    }

    public void incrementLastChatIndex() {
        this.lastChatIndex++;
    }

    public void resetLastChatIndex() {
        this.lastChatIndex = 0;
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(-this.id);
        message.appendString(this.name);
        message.appendString(this.motto);
        message.appendString(this.figure);
        message.appendInt(this.roomUnit.getVirtualId());
        message.appendInt(this.roomUnit.getCurrentPosition() == null ? 0 : this.roomUnit.getCurrentPosition().getX());
        message.appendInt(this.roomUnit.getCurrentPosition() == null ? 0 : this.roomUnit.getCurrentPosition().getY());
        message.appendString(String.valueOf(this.roomUnit.getCurrentZ()));
        message.appendInt(this.roomUnit.getBodyRotation().getValue());
        message.appendInt(4);
        message.appendString(this.gender.name().toUpperCase());
        message.appendInt(this.ownerId);
        message.appendString(this.ownerName);
        message.appendInt(10);
        message.appendShort(0);
        message.appendShort(1);
        message.appendShort(2);
        message.appendShort(3);
        message.appendShort(4);
        message.appendShort(5);
        message.appendShort(6);
        message.appendShort(7);
        message.appendShort(8);
        message.appendShort(9);
    }
}
