package com.eu.habbo.plugin.events.guilds.forums;

import com.eu.habbo.roleplay.guilds.forums.ForumThreadComment;
import com.eu.habbo.plugin.Event;

public class GuildForumThreadCommentCreated extends Event {
    public final ForumThreadComment comment;

    public GuildForumThreadCommentCreated(ForumThreadComment comment) {
        this.comment = comment;
    }
}
