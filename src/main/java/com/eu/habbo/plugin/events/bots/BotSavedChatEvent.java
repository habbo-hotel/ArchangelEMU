package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class BotSavedChatEvent extends BotEvent {

    private final boolean autoChat;


    private final boolean randomChat;


    private final int chatDelay;


    private final ArrayList<String> chat;


    public BotSavedChatEvent(Bot bot, boolean autoChat, boolean randomChat, int chatDelay, ArrayList<String> chat) {
        super(bot);

        this.autoChat = autoChat;
        this.randomChat = randomChat;
        this.chatDelay = chatDelay;
        this.chat = chat;
    }
}
