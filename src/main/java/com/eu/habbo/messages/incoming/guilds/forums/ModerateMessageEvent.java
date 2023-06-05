package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadState;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.UpdateMessageMessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;


public class ModerateMessageEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int messageId = packet.readInt();
        int state = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        ForumThread thread = ForumThread.getById(threadId);

        if (guild == null || thread == null) {
            this.client.sendResponse(new ErrorReportComposer(404));
            return;
        }

        ForumThreadComment comment = thread.getCommentById(messageId);
        if (comment == null) {
            this.client.sendResponse(new ErrorReportComposer(404));
            return;
        }

        boolean hasStaffPermissions = this.client.getHabbo().hasRight(Permission.ACC_MODTOOL_TICKET_Q);

        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        if (member == null) {
            this.client.sendResponse(new ErrorReportComposer(401));
            return;
        }

        boolean isGuildAdministrator = (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || member.getRank().equals(GuildRank.ADMIN));

        if (!isGuildAdministrator && !hasStaffPermissions) {
            this.client.sendResponse(new ErrorReportComposer(403));
            return;
        }

        if (state == ForumThreadState.HIDDEN_BY_GUILD_ADMIN.getStateId() && !hasStaffPermissions) {
            this.client.sendResponse(new ErrorReportComposer(403));
            return;
        }

        comment.setState(ForumThreadState.fromValue(state));
        comment.setAdminId(this.client.getHabbo().getHabboInfo().getId());
        this.client.sendResponse(new UpdateMessageMessageComposer(guild.getId(), thread.getThreadId(), comment));

        Emulator.getThreading().run(comment);

        switch (state) {
            case 10, 20 ->
                    this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FORUMS_MESSAGE_HIDDEN.getKey()).compose());
            case 1 ->
                    this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FORUMS_MESSAGE_RESTORED.getKey()).compose());
        }

    }
}