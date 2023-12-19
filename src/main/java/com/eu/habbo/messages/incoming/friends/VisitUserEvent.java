package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.chat.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FollowFriendFailedComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.WhisperMessageComposer;

public class VisitUserEvent extends MessageHandler {
    @Override
    public void handle() {
        int friendId = this.packet.readInt();

        MessengerBuddy buddy = this.client.getHabbo().getMessenger().getFriend(friendId);

        if (buddy == null) {
            this.client.sendResponse(new FollowFriendFailedComposer(FollowFriendFailedComposer.NOT_IN_FRIEND_LIST));
            return;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(friendId);

        if (habbo == null || !habbo.isOnline()) {
            this.client.sendResponse(new FollowFriendFailedComposer(FollowFriendFailedComposer.FRIEND_OFFLINE));
            return;
        }

        if (habbo.getHabboStats().isBlockFollowing() && !this.client.getHabbo().hasPermissionRight(Permission.ACC_CAN_STALK)) {
            this.client.sendResponse(new FollowFriendFailedComposer(FollowFriendFailedComposer.FRIEND_BLOCKED_STALKING));
            return;
        }

        if (habbo.getRoomUnit().getRoom() == null) {
            this.client.sendResponse(new FollowFriendFailedComposer(FollowFriendFailedComposer.FRIEND_NOT_IN_ROOM));
            return;
        }

        if (habbo.getRoomUnit().getRoom() != this.client.getHabbo().getRoomUnit().getRoom()) {
            this.client.sendResponse(new RoomForwardMessageComposer(habbo.getRoomUnit().getRoom().getRoomInfo().getId()));
        } else {
            this.client.sendResponse(new WhisperMessageComposer(new RoomChatMessage(Emulator.getTexts().getValue("stalk.failed.same.room").replace("%user%", habbo.getHabboInfo().getUsername()), this.client.getHabbo(), this.client.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
    }
}
