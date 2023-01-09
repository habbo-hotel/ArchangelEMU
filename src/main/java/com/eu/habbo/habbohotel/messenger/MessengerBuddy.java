package com.eu.habbo.habbohotel.messenger;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.WordFilter;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.friends.NewConsoleMessageComposer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MessengerBuddy implements Runnable, ISerialize {

    @Getter
    private int id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private HabboGender gender = HabboGender.M;
    @Getter
    private int online = 0;
    @Setter
    @Getter
    private String look = "";
    @Getter
    private String motto = "";
    @Getter
    private short relation;
    @Getter
    private int categoryId = 0;
    private boolean inRoom;
    private int userOne = 0;

    public MessengerBuddy(ResultSet set) {
        try {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.gender = HabboGender.valueOf(set.getString("gender"));
            this.online = set.getInt("online");
            this.motto = set.getString("motto");
            this.look = set.getString("look");
            this.relation = (short) set.getInt("relation");
            this.categoryId = set.getInt("category");
            this.userOne = set.getInt("user_one_id");
            this.inRoom = false;
            if (this.online == 1) {
                Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(this.username);

                if (habbo != null) {
                    this.inRoom = habbo.getHabboInfo().getCurrentRoom() != null;
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public MessengerBuddy(ResultSet set, boolean value) {
        try {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.look = set.getString("look");
            this.relation = 0;
            this.userOne = 0;
            this.online = set.getInt("online");
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public MessengerBuddy(int id, String username, String look, Short relation, int userOne) {
        this.id = id;
        this.username = username;
        this.gender = HabboGender.M;
        this.online = 0;
        this.motto = "";
        this.look = look;
        this.relation = relation;
        this.userOne = userOne;
    }

    public MessengerBuddy(Habbo habbo, int userOne) {
        this.id = habbo.getHabboInfo().getId();
        this.username = habbo.getHabboInfo().getUsername();
        this.gender = habbo.getHabboInfo().getGender();
        this.online = habbo.getHabboInfo().isOnline() ? 1 : 0;
        this.motto = habbo.getHabboInfo().getMotto();
        this.look = habbo.getHabboInfo().getLook();
        this.relation = 0;
        this.userOne = userOne;
        this.inRoom = habbo.getHabboInfo().getCurrentRoom() != null;
    }

    public void setOnline(boolean value) {
        this.online = (value ? 1 : 0);
    }

    public void setRelation(int relation) {
        this.relation = (short) relation;
        Emulator.getThreading().run(this);
    }

    public boolean inRoom() {
        return this.inRoom;
    }

    public void inRoom(boolean value) {
        this.inRoom = value;
    }

    @Override
    public void run() {

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE messenger_friendships SET relation = ? WHERE user_one_id = ? AND user_two_id = ?")) {
            statement.setInt(1, this.relation);
            statement.setInt(2, this.userOne);
            statement.setInt(3, this.id);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public void onMessageReceived(Habbo from, String message) {
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(this.id);

        if (habbo == null)
            return;

        Message chatMessage = new Message(from.getHabboInfo().getId(), this.id, message);
        Emulator.getThreading().run(chatMessage);

        if (WordFilter.ENABLED_FRIENDCHAT) {
            chatMessage.setMessage(Emulator.getGameEnvironment().getWordFilter().filter(chatMessage.getMessage(), from));
        }

        habbo.getClient().sendResponse(new NewConsoleMessageComposer(chatMessage));
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.id);
        message.appendString(this.username);
        message.appendInt(this.gender.equals(HabboGender.M) ? 0 : 1);
        message.appendBoolean(this.online == 1);
        message.appendBoolean(this.inRoom); //IN ROOM
        message.appendString(this.look);
        message.appendInt(this.categoryId); // Friends category ID
        message.appendString(this.motto);
        message.appendString(""); //Last seen as DATETIMESTRING
        message.appendString(""); // Realname or Facebookame as String
        message.appendBoolean(false); //Offline messaging.
        message.appendBoolean(false);
        message.appendBoolean(false);
        message.appendShort(this.relation);
    }
}
