package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.GuildPart;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildEditorDataMessageComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildEditorDataMessageComposer);
        this.response.appendInt(Emulator.getGameEnvironment().getGuildManager().getBases().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBases()) {
            this.response.appendInt(part.getId());
            this.response.appendString(part.getValueA());
            this.response.appendString(part.getValueB());
        }

        this.response.appendInt(Emulator.getGameEnvironment().getGuildManager().getSymbols().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getSymbols()) {
            this.response.appendInt(part.getId());
            this.response.appendString(part.getValueA());
            this.response.appendString(part.getValueB());
        }

        this.response.appendInt(Emulator.getGameEnvironment().getGuildManager().getBaseColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBaseColors()) {
            this.response.appendInt(part.getId());
            this.response.appendString(part.getValueA());
        }

        this.response.appendInt(Emulator.getGameEnvironment().getGuildManager().getSymbolColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getSymbolColors()) {
            this.response.appendInt(part.getId());
            this.response.appendString(part.getValueA());
        }

        this.response.appendInt(Emulator.getGameEnvironment().getGuildManager().getBackgroundColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBackgroundColors()) {
            this.response.appendInt(part.getId());
            this.response.appendString(part.getValueA());
        }

        return this.response;
    }
}
