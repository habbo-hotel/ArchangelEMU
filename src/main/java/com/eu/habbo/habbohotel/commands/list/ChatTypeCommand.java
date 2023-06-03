package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.users.AccountPreferencesComposer;

public class ChatTypeCommand extends Command {
    public ChatTypeCommand() {
        super("cmd_chat_color");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        if (params.length >= 2) {
            int chatColor;
            try {
                chatColor = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_chatcolor.numbers"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (RoomChatMessageBubbles.values().length < chatColor) {
                chatColor = 0;
            }

            if (chatColor < 0) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_chatcolor.numbers"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (!gameClient.getHabbo().hasRight(Permission.ACC_ANYCHATCOLOR)) {
                for (String s : Emulator.getConfig().getValue("commands.cmd_chatcolor.banned_numbers").split(";")) {
                    if (Integer.parseInt(s) == chatColor) {
                        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_chatcolor.banned"), RoomChatMessageBubbles.ALERT);
                        return true;
                    }
                }
            }

            gameClient.getHabbo().getHabboStats().setChatColor(RoomChatMessageBubbles.getBubble(chatColor));
            gameClient.sendResponse(new AccountPreferencesComposer(gameClient.getHabbo()));
            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_chatcolor.set").replace("%chat%", RoomChatMessageBubbles.values()[chatColor].name().replace("_", " ").toLowerCase()), RoomChatMessageBubbles.ALERT);
        } else {
            gameClient.getHabbo().getHabboStats().setChatColor(RoomChatMessageBubbles.NORMAL);
            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_chatcolor.reset"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
