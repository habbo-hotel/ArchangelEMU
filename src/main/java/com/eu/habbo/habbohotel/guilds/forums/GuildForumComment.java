package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GuildForumComment implements ISerialize, Runnable {
    private int id;
    private final int guildId;
    private final int threadId;
    private int index = -1;
    private final int userId;
    private final String userName;
    private final String look;
    private final int timestamp;
    private final String message;
    private GuildForum.ThreadState state = GuildForum.ThreadState.OPEN;
    private int adminId;
    private String adminName;

    public GuildForumComment(int guildId, int threadId, int userId, String userName, String look, String message) {
        this.guildId = guildId;
        this.threadId = threadId;
        this.userId = userId;
        this.userName = userName;
        this.look = look;
        this.timestamp = Emulator.getIntUnixTimestamp();
        this.message = message;
        this.adminName = "";
    }

    public GuildForumComment(final ResultSet set, int index, int guildId) throws SQLException {
        this.id = set.getInt("id");
        this.guildId = guildId;
        this.threadId = set.getInt("thread_id");
        this.index = index;
        this.userId = set.getInt("user_id");
        this.userName = set.getString("author_name");
        this.look = set.getString("look");
        this.timestamp = set.getInt("timestamp");
        this.message = set.getString("message");
        this.state = GuildForum.ThreadState.valueOf(set.getString("state"));
        this.adminId = set.getInt("admin_id");
        this.adminName = set.getString("admin_name");
    }

    public int getAuthorPostCount() {
        GuildForum guildForum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(this.guildId);

        List<GuildForumComment> matchingObjects = guildForum.getThreads().stream().flatMap(e -> e.getAllComments().stream()).collect(Collectors.toList()).stream().filter(c -> c.getUserId() == this.userId).collect(Collectors.toList());

        return matchingObjects.size();
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.id);
        message.appendInt(this.index - 1);
        message.appendInt(this.userId);
        message.appendString(this.userName);
        message.appendString(this.look);
        message.appendInt(Emulator.getIntUnixTimestamp() - this.timestamp);
        message.appendString(this.message);
        message.appendByte(this.state.state);
        message.appendInt(this.adminId);
        message.appendString(this.adminName);
        message.appendInt(0);
        message.appendInt(this.getAuthorPostCount());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getLook() {
        return this.look;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public GuildForum.ThreadState getState() {
        return this.state;
    }

    public void setState(GuildForum.ThreadState state) {
        this.state = state;
    }

    public int getAdminId() {
        return this.adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public void run() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guilds_forums_comments SET state = ?, admin_id = ? WHERE id = ?")) {
            statement.setString(1, this.state.name());
            statement.setInt(2, this.adminId);
            statement.setInt(3, this.getId());
            statement.execute();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public int getGuildId() {
        return this.guildId;
    }
}