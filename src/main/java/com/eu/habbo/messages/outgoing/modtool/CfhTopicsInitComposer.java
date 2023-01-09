package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CfhTopicsInitComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.cfhTopicsInitComposer);

        this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getCfhCategories().valueCollection().size());

        Emulator.getGameEnvironment().getModToolManager().getCfhCategories().forEachValue(category -> {
            CfhTopicsInitComposer.this.response.appendString(category.getName());
            CfhTopicsInitComposer.this.response.appendInt(category.getTopics().valueCollection().size());
            category.getTopics().forEachValue(topic -> {
                CfhTopicsInitComposer.this.response.appendString(topic.getName());
                CfhTopicsInitComposer.this.response.appendInt(topic.getId());
                CfhTopicsInitComposer.this.response.appendString(topic.getAction().toString());
                return true;
            });
            return true;
        });

        return this.response;
    }
}