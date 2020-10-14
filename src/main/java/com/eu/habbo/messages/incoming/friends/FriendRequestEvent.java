package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.FriendRequest;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestComposer;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import com.eu.habbo.plugin.events.users.friends.UserRequestFriendshipEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FriendRequestEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendRequestEvent.class);

    @Override
    public void handle() throws Exception {
        String username = this.packet.readString();
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

        if (habbo.getHabboInfo().getId() == this.client.getHabbo().getHabboInfo().getId()) {
            return;
        }

        if (Emulator.getPluginManager().fireEvent(new UserRequestFriendshipEvent(this.client.getHabbo(), username, habbo)).isCancelled()) {
            this.client.sendResponse(new FriendRequestErrorComposer(2));
            return;
        }

        int id = 0;
        boolean allowFriendRequests = true;

        FriendRequest friendRequest = this.client.getHabbo().getMessenger().findFriendRequest(username);
        if (friendRequest != null) {
            this.client.getHabbo().getMessenger().acceptFriendRequest(friendRequest.getId(), this.client.getHabbo().getHabboInfo().getId());
            return;
        }

        if (!Messenger.canFriendRequest(this.client.getHabbo(), username)) {
            this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
            return;
        }

        if (habbo == null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users_settings.block_friendrequests, users.id FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1")) {
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        id = set.getInt("id");
                        allowFriendRequests = set.getString("block_friendrequests").equalsIgnoreCase("0");
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
                return;
            }
        } else {
            id = habbo.getHabboInfo().getId();
            allowFriendRequests = !habbo.getHabboStats().blockFriendRequests;
            if (allowFriendRequests)
                habbo.getClient().sendResponse(new FriendRequestComposer(this.client.getHabbo()));
        }

        if (id != 0) {
            if (!allowFriendRequests) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_ACCEPTING_REQUESTS));
                return;
            }

            if (this.client.getHabbo().getMessenger().getFriends().values().size() >= this.client.getHabbo().getHabboStats().maxFriends && !this.client.getHabbo().hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_OWN_FULL));
                return;
            }

            if (habbo.getMessenger().getFriends().values().size() >= habbo.getHabboStats().maxFriends && !habbo.hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_TARGET_FULL));
                return;
            }

            Messenger.makeFriendRequest(this.client.getHabbo().getHabboInfo().getId(), id);
        } else {
            this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
        }
    }
}
