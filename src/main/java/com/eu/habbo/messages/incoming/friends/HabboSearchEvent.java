package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.HabboSearchResultComposer;
import gnu.trove.set.hash.THashSet;

import java.util.concurrent.ConcurrentHashMap;

public class HabboSearchEvent extends MessageHandler {
    public static final ConcurrentHashMap<String, THashSet<MessengerBuddy>> cachedResults = new ConcurrentHashMap<>();

    @Override
    public void handle() {
        if (System.currentTimeMillis() - this.client.getHabbo().getHabboStats().getLastUsersSearched() < 3000)
            return;

        String username = this.packet.readString().replace(" ", "").toLowerCase();

        if (username.isEmpty())
            return;

        if (username.length() > 15) {
            username = username.substring(0, 15);
        }

        if (this.client.getHabbo().getMessenger() != null) {
            THashSet<MessengerBuddy> buddies = cachedResults.get(username);

            if (buddies == null) {
                buddies = Messenger.searchUsers(username);
                cachedResults.put(username, buddies);
            }

            this.client.sendResponse(new HabboSearchResultComposer(buddies, this.client.getHabbo().getMessenger().getFriends(username), this.client.getHabbo()));
        }
    }
}
