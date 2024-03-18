package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.plugin.events.guilds.forums.GuildForumThreadCommentBeforeCreated;
import com.eu.habbo.plugin.events.guilds.forums.GuildForumThreadCommentCreated;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
public class ForumThreadComment implements Runnable, ISerialize {

    private static final THashMap<Integer, ForumThreadComment> forumCommentsCache = new THashMap<>();
    @Getter
    private final int commentId;
    @Getter
    private final int threadId;
    @Getter
    private final int userId;
    @Getter
    private final String message;
    @Getter
    private final int createdAt;
    @Getter
    private ForumThreadState state;
    @Getter
    private int adminId;
    @Setter
    @Getter
    private int index = -1;
    private boolean needsUpdate = false;

    public ForumThreadComment(int commentId, int threadId, int userId, String message, int createdAt, ForumThreadState state, int adminId) {
        this.commentId = commentId;
        this.threadId = threadId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.state = state;
        this.adminId = adminId;
    }

    public ForumThreadComment(ResultSet set) throws SQLException {
        this.commentId = set.getInt("id");
        this.threadId = set.getInt("thread_id");
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.message = set.getString("message");
        this.createdAt = set.getInt("created_at");
        this.state = ForumThreadState.fromValue(set.getInt("state"));
        this.adminId = set.getInt("admin_id");
        this.index = -1;
        this.needsUpdate = false;
    }

    public static ForumThreadComment getById(int id) {
        ForumThreadComment foundComment = forumCommentsCache.get(id);

        if (foundComment != null)
            return foundComment;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM `guilds_forums_comments` WHERE `id` = ? LIMIT 1")) {
            statement.setInt(1, id);

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    foundComment = new ForumThreadComment(set);
                    cacheComment(foundComment);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return foundComment;
    }

    public static void cacheComment(ForumThreadComment foundComment) {
        forumCommentsCache.put(foundComment.commentId, foundComment);
    }

    public static void clearCache() {
        forumCommentsCache.clear();
    }

    public static ForumThreadComment create(ForumThread thread, Habbo poster, String message) {
        ForumThreadComment createdComment = null;

        if (Emulator.getPluginManager().fireEvent(new GuildForumThreadCommentBeforeCreated(thread, poster, message)).isCancelled())
            return null;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `guilds_forums_comments`(`thread_id`, `user_id`, `message`, `created_at`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            int timestamp = Emulator.getIntUnixTimestamp();

            statement.setInt(1, thread.getThreadId());
            statement.setInt(2, poster.getHabboInfo().getId());
            statement.setString(3, message);
            statement.setInt(4, timestamp);

            if (statement.executeUpdate() < 1)
                return null;

            ResultSet set = statement.getGeneratedKeys();
            if (set.next()) {
                int commentId = set.getInt(1);
                createdComment = new ForumThreadComment(commentId, thread.getThreadId(), poster.getHabboInfo().getId(), message, timestamp, ForumThreadState.OPEN, 0);

                Emulator.getPluginManager().fireEvent(new GuildForumThreadCommentCreated(createdComment));
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return createdComment;
    }

    public void setState(ForumThreadState state) {
        this.state = state;
        this.needsUpdate = true;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        this.needsUpdate = true;
    }

    public Habbo getHabbo() {
        return Emulator.getGameEnvironment().getHabboManager().getHabbo(this.userId);
    }

    public ForumThread getThread() {
        return ForumThread.getById(this.threadId);
    }

    @Override
    public void serialize(ServerMessage message) {

        HabboInfo habbo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(this.userId);
        HabboInfo admin = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(this.adminId);

        message.appendInt(this.commentId);
        message.appendInt(this.index);
        message.appendInt(this.userId);
        message.appendString(habbo != null ? habbo.getUsername() : "");
        message.appendString(habbo != null ? habbo.getLook() : "");
        message.appendInt(Emulator.getIntUnixTimestamp() - this.createdAt);
        message.appendString(this.message);
        message.appendByte(this.state.getStateId());
        message.appendInt(this.adminId);
        message.appendString(admin != null ? admin.getUsername() : "");
        message.appendInt(0); // admin action time ago?
        message.appendInt(habbo != null ? habbo.getHabboStats().getForumPostsCount() : 0);
    }

    @Override
    public void run() {
        if (!this.needsUpdate)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guilds_forums_comments SET `state` = ?, `admin_id` = ? WHERE `id` = ?")) {
            statement.setInt(1, this.state.getStateId());
            statement.setInt(2, this.adminId);
            statement.setInt(3, this.commentId);
            statement.execute();

            this.needsUpdate = false;
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }
}
