package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import com.eu.habbo.plugin.PluginManager;

public class AcceptFriendRequestEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int count = this.packet.readInt();
        int userId;

        for (int i = 0; i < count; i++) {
            userId = this.packet.readInt();

            if (userId == 0)
                return;

            if (this.client.getHabbo().getMessenger().getFriends().containsKey(userId)) {
                this.client.getHabbo().getMessenger().deleteFriendRequests(userId, this.client.getHabbo().getHabboInfo().getId());
                continue;
            }

            Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(target == null) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
                this.client.getHabbo().getMessenger().deleteFriendRequests(userId, this.client.getHabbo().getHabboInfo().getId());
                continue;
            }

            if(this.client.getHabbo().getMessenger().getFriends().size() >= this.client.getHabbo().getHabboStats().maxFriends && !this.client.getHabbo().hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_OWN_FULL));
                break;
            }

            if(target.getMessenger().getFriends().size() >= target.getHabboStats().maxFriends && !target.hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_TARGET_FULL));
                continue;
            }

            this.client.getHabbo().getMessenger().acceptFriendRequest(userId, this.client.getHabbo().getHabboInfo().getId());

            Messenger.checkFriendSizeProgress(this.client.getHabbo());
            Messenger.checkFriendSizeProgress(target);
        }
    }
}
