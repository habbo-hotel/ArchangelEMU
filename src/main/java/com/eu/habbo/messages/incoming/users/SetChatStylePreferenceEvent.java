package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetChatStylePreferenceEvent extends MessageHandler {
    @Override
    public void handle() {
        int chatBubble = this.packet.readInt();

        if (!this.client.getHabbo().hasRight(Permission.ACC_ANYCHATCOLOR)) {
            for (String s : Emulator.getConfig().getValue("commands.cmd_chatcolor.banned_numbers").split(";")) {
                if (Integer.parseInt(s) == chatBubble) {
                    return;
                }
            }
        }

        this.client.getHabbo().getHabboStats().setChatColor(RoomChatMessageBubbles.getBubble(chatBubble));
    }
}
