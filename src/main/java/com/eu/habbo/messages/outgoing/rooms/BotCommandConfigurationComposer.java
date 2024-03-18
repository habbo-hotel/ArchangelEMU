package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BotCommandConfigurationComposer extends MessageComposer {
    private final Bot bot;
    private final int settingId;

    public BotCommandConfigurationComposer(Bot bot, int settingId) {
        this.bot = bot;
        this.settingId = settingId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.botCommandConfigurationComposer);
        this.response.appendInt(-this.bot.getId());
        this.response.appendInt(this.settingId);

        switch (this.settingId) {
            case 1, 3, 4, 6 -> this.response.appendString("");
            case 2 -> {
                StringBuilder data = new StringBuilder();
                if (this.bot.hasChat()) {
                    for (String s : this.bot.getChatLines()) {
                        data.append(s).append("\r");
                    }
                } else {
                    data.append(Bot.NO_CHAT_SET);
                }
                data.append(";#;").append(this.bot.isChatAuto() ? "true" : "false");
                data.append(";#;").append(this.bot.getChatDelay());
                data.append(";#;").append(this.bot.isChatRandom() ? "true" : "false");
                this.response.appendString(data.toString());
            }
            case 5 -> this.response.appendString(this.bot.getName());
            case 9 -> this.response.appendString(this.bot.getMotto());
        }
        return this.response;
    }
}
