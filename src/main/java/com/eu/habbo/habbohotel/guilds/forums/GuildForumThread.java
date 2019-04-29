package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.THashMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuildForumThread implements ISerialize, Runnable {
    private final int threadId;
    private final int guildId;
    private final int authorId;
    private final String authorName;
    private final String subject;
    private final String message;
    private GuildForum.ThreadState state;
    private final int timestamp;
    private boolean pinned = false;
    private boolean locked = false;

    private int lastAuthorId = 0;
    private String lastAuthorName;

    public int getLastCommentTimestamp() {
        return this.lastCommentTimestamp;
    }

    private int lastCommentTimestamp = 0;
    private int adminId;
    private String adminName = "";

    private int commentsIndex = 1;



    public final THashMap<Integer, GuildForumComment> comments;

    public GuildForumThread(Habbo habbo, int threadId, int guildId, String subject, String message, int timestamp) {
        this.threadId = threadId;
        this.guildId = guildId;
        this.authorId = habbo.getHabboInfo().getId();
        this.authorName = habbo.getHabboInfo().getUsername();
        this.subject = subject;
        this.message = message;
        this.state = GuildForum.ThreadState.OPEN;
        this.timestamp = timestamp;
        this.lastAuthorId = this.authorId;
        this.lastAuthorName = this.authorName;
        this.lastCommentTimestamp = this.timestamp;


        this.comments = new THashMap<>();
    }


    public GuildForumThread(ResultSet set) throws SQLException {
        this.threadId = set.getInt("id");
        this.guildId = set.getInt("guild_id");
        this.authorId = set.getInt("user_id");
        this.authorName = set.getString("author_name");
        this.subject = set.getString("subject");
        this.message = set.getString("message");
        this.state = GuildForum.ThreadState.valueOf(set.getString("state"));
        this.timestamp = set.getInt("timestamp");
        this.pinned = set.getString("pinned").equals("1");
        this.locked = set.getString("locked").equals("1");
        this.adminId = set.getInt("admin_id");
        this.adminName = set.getString("admin_name");


        this.lastAuthorId = this.authorId;
        this.lastAuthorName = this.authorName;
        this.lastCommentTimestamp = this.timestamp;

        this.comments = new THashMap<>();
        this.comments.put(commentsIndex, new GuildForumComment(set, commentsIndex, guildId));
        commentsIndex++;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT " +
                "author.username AS author_name, " +
                "COALESCE(admin.username, '') as admin_name, " +
                "author.look, " +
                "guilds_forums_comments.* " +
                "FROM guilds_forums_comments " +
                "INNER JOIN users AS author ON guilds_forums_comments.user_id = author.id " +
                "LEFT JOIN users AS admin  ON guilds_forums_comments.admin_id = admin.id " +
                "WHERE thread_id = ? " +
                "ORDER BY id ASC")) {
            statement.setInt(1, this.threadId);
            try (ResultSet commentSet = statement.executeQuery()) {
                while (commentSet.next()) {
                    this.comments.put(commentsIndex, new GuildForumComment(commentSet, commentsIndex, this.guildId));
                    commentsIndex++;
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addComment(GuildForumComment comment) {
        synchronized (this.comments) {
            comment.setIndex(commentsIndex);
            this.comments.put(commentsIndex, comment);
            commentsIndex++;
        }

        this.lastAuthorId = comment.getUserId();
        this.lastAuthorName = comment.getUserName();
        this.lastCommentTimestamp = comment.getTimestamp();
    }

    public GuildForumComment addComment(Habbo habbo, String message) {
        int commentId = -1;

        GuildForumComment comment = new GuildForumComment(this.guildId, this.threadId, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername(), habbo.getHabboInfo().getLook(), message);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds_forums_comments (thread_id, user_id, timestamp, message) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.threadId);
            statement.setInt(2, habbo.getHabboInfo().getId());
            int nowTimestamp = Emulator.getIntUnixTimestamp();
            statement.setInt(3, nowTimestamp);
            statement.setString(4, message);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    commentId = set.getInt(1);
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        if (commentId >= 0) {
            comment.setId(commentId);
            addComment(comment);

            return comment;
        }

        return null;
    }

    public GuildForumComment getCommentById(int id) {
        synchronized (this.comments) {
            for(GuildForumComment comment : this.comments.values()) {
                if(comment.getId() == id) {
                    return comment;
                }
            }
        }

        return null;
    }

    public GuildForumComment getCommentByIndex(int id) {
        synchronized (this.comments) {
            return this.comments.get(id);
        }
    }


    /* Original Group Forum Code By Claudio and TheGeneral.
    Rewritten because it was terrible.
    Credits To Beny.
     */
    public List<GuildForumComment> getComments(int page, int limit) {

        List<GuildForumComment> allComments = new ArrayList(this.comments.values());

        Collections.reverse(allComments);

        List<GuildForumComment> comments = new ArrayList<>();

        int start = page;
        int end = start + limit;

        int i = 0;
        synchronized (this.comments) {
            for(GuildForumComment comment : allComments) {
                if(i >= start && i < end) {
                    comments.add(comment);
                }
                i++;
            }
        }

        return comments;
    }

    public Collection<GuildForumComment> getAllComments() {
        synchronized (this.comments) {
            return this.comments.values();
        }
    }

    public Integer getAmountOfComments() {
        synchronized (this.comments) {
            return this.comments.size();
        }
    }

    public int getId() {
        return this.threadId;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public int getCommentsSize() {
        return this.comments.size();
    }

    public String getSubject() {
        return this.subject;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public int getAuthorId() {
        return this.authorId;
    }

    public String getAuthorName() {
        return this.authorName;
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

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }



    public boolean isPinned() {
        return pinned;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void serialize(ServerMessage message) {
        int nowTimestamp = Emulator.getIntUnixTimestamp();
        message.appendInt(this.threadId);
        message.appendInt(this.authorId);
        message.appendString(this.authorName);
        message.appendString(this.subject);
        message.appendBoolean(this.pinned);
        message.appendBoolean(this.locked);
        message.appendInt(nowTimestamp - this.timestamp);
        message.appendInt(this.getCommentsSize());
        message.appendInt(0);
        message.appendInt(1);
        message.appendInt(this.lastAuthorId);
        message.appendString(this.lastAuthorName);
        message.appendInt(nowTimestamp - this.lastCommentTimestamp);
        message.appendByte(this.state.state);
        message.appendInt(this.adminId);
        message.appendString(this.adminName);
        message.appendInt(this.threadId);
    }


    @Override
    public void run() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guilds_forums SET message = ?, state = ?, pinned = ?, locked = ?, admin_id = ? WHERE id = ?")) {
            statement.setString(1, this.message);
            statement.setString(2, this.state.name());
            statement.setString(3, this.pinned ? "1" : "0");
            statement.setString(4, this.locked ? "1" : "0");
            statement.setInt(5, this.adminId);
            statement.setInt(6, this.getId());
            statement.execute();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }
}