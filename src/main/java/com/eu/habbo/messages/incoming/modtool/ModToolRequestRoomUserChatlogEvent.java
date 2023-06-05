package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.RoomChatlogComposer;

public class ModToolRequestRoomUserChatlogEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().hasRight(Permission.ACC_SUPPORTTOOL)) {
            int userId = this.packet.readInt();

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null) {
                Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

                if (room != null) {
                    this.client.sendResponse(new RoomChatlogComposer(room, Emulator.getGameEnvironment().getModToolManager().getRoomChatlog(room.getId())));
                }
            }
        } else {
            ScripterManager.scripterDetected(this.client, Emulator.getTexts().getValue("scripter.warning.chatlog").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
