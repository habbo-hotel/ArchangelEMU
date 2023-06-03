package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.MessengerErrorComposer;
import com.eu.habbo.messages.outgoing.friends.NewFriendRequestComposer;
import com.eu.habbo.plugin.events.users.friends.UserRequestFriendshipEvent;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Slf4j
public class RequestFriendEvent extends MessageHandler {
    @Override
    public void handle() {
        String username = this.packet.readString();

        if (this.client == null || username == null || username.isEmpty())
            return;

        // TargetHabbo can be null if the Habbo is not online or when the Habbo doesn't exist
        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

        // If the Habbo is null, we try to get the Habbo from the database.
        // If the Habbo is still null, the Habbo doesn't exist.
        if (targetHabbo == null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.*, users_settings.block_friendrequests FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1")) {
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        targetHabbo = new Habbo(set);
                    }
                }
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
                return;
            }
        }

        if (targetHabbo == null) {
            this.client.sendResponse(new MessengerErrorComposer(MessengerErrorComposer.TARGET_NOT_FOUND));
            return;
        }

        int targetId = targetHabbo.getHabboInfo().getId();
        boolean targetBlocksFriendRequests = targetHabbo.getHabboStats().isBlockFriendRequests();

        // Making friends with yourself would be very pathetic, we try to avoid that
        if (targetId == this.client.getHabbo().getHabboInfo().getId())
            return;

        // Target Habbo exists
        // Check if Habbo is accepting friend requests
        if (targetBlocksFriendRequests) {
            this.client.sendResponse(new MessengerErrorComposer(MessengerErrorComposer.TARGET_NOT_ACCEPTING_REQUESTS));
            return;
        }

        // You can only have x friends
        if (this.client.getHabbo().getMessenger().getFriends().values().size() >= this.client.getHabbo().getHabboStats().getMaxFriends() && !this.client.getHabbo().hasRight(Permission.ACC_INFINITE_FRIENDS)) {
            this.client.sendResponse(new MessengerErrorComposer(MessengerErrorComposer.FRIEND_LIST_OWN_FULL));
            return;
        }

        // Check if targets friendlist is full
        if (targetHabbo.getMessenger().getFriends().values().size() >= targetHabbo.getHabboStats().getMaxFriends() && !targetHabbo.hasRight(Permission.ACC_INFINITE_FRIENDS)) {
            this.client.sendResponse(new MessengerErrorComposer(MessengerErrorComposer.FRIEND_LIST_TARGET_FULL));
            return;
        }

        // Allow plugins to cancel the request
        if (Emulator.getPluginManager().fireEvent(new UserRequestFriendshipEvent(this.client.getHabbo(), username, targetHabbo)).isCancelled()) {
            this.client.sendResponse(new MessengerErrorComposer(2));
            return;
        }

        if(targetHabbo.isOnline()) {
            targetHabbo.getClient().sendResponse(new NewFriendRequestComposer(this.client.getHabbo().getHabboInfo()));
        }

        Messenger.makeFriendRequest(this.client.getHabbo().getHabboInfo().getId(), targetId);
    }
}
