package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.PostMessageMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.PostThreadMessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;


public class PostMessageEvent extends MessageHandler {

    @Override
    public int getRatelimit() {
        return 1000;
    }
    
    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt();
        String subject = Emulator.getGameEnvironment().getWordFilter().filter(this.packet.readString(), this.client.getHabbo());
        String message = Emulator.getGameEnvironment().getWordFilter().filter(this.packet.readString(), this.client.getHabbo());

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null) {
            this.client.sendResponse(new ErrorReportComposer(404));
            return;
        }

        if (message.length() < 10 || message.length() > 4000 || (threadId == 0 && (subject.length() < 10 || subject.length() > 120))) {
            this.client.sendResponse(new ErrorReportComposer(400));
            return;
        }

        boolean isStaff = this.client.getHabbo().hasRight(Permission.ACC_MODTOOL_TICKET_Q);

        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());

        ForumThread thread = ForumThread.getById(threadId);

        if (threadId == 0) {
            if (!((guild.canPostThreads().getState() == 0)
                    || (guild.canPostThreads().getState() == 1 && member != null)
                    || (guild.canPostThreads().getState() == 2 && member != null && (member.getRank().getType() < GuildRank.MEMBER.getType()))
                    || (guild.canPostThreads().getState() == 3 && guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId())
                    || isStaff)) {
                this.client.sendResponse(new ErrorReportComposer(403));
                return;
            }


            thread = ForumThread.create(guild, this.client.getHabbo(), subject, message);

            if (thread == null) {
                this.client.sendResponse(new ErrorReportComposer(500));
                return;
            }

            this.client.getHabbo().getHabboStats().setForumPostsCount(this.client.getHabbo().getHabboStats().getForumPostsCount()+1);
            thread.setPostsCount(thread.getPostsCount() + 1);
            this.client.sendResponse(new PostThreadMessageComposer(thread));
            return;
        }

        if (thread == null) {
            this.client.sendResponse(new ErrorReportComposer(404));
            return;
        }


        if (!((guild.canPostMessages().getState() == 0)
                || (guild.canPostMessages().getState() == 1 && member != null)
                || (guild.canPostMessages().getState() == 2 && member != null && (member.getRank().getType() < GuildRank.MEMBER.getType()))
                || (guild.canPostMessages().getState() == 3 && guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId())
                || isStaff)) {
            this.client.sendResponse(new ErrorReportComposer(403));
            return;
        }

        ForumThreadComment comment = ForumThreadComment.create(thread, this.client.getHabbo(), message);

        if (comment != null) {
            thread.addComment(comment);
            thread.setUpdatedAt(Emulator.getIntUnixTimestamp());
            this.client.getHabbo().getHabboStats().setForumPostsCount(this.client.getHabbo().getHabboStats().getForumPostsCount()+1);
            thread.setPostsCount(thread.getPostsCount() + 1);
            this.client.sendResponse(new PostMessageMessageComposer(comment));
        } else {
            this.client.sendResponse(new ErrorReportComposer(500));
        }
    }
}