package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.CfhCategory;
import com.eu.habbo.habbohotel.modtool.CfhTopic;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TObjectProcedure;

public class CfhTopicsInitComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.CfhTopicsInitComposer);

        this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getCfhCategories().valueCollection().size());

        Emulator.getGameEnvironment().getModToolManager().getCfhCategories().forEachValue(new TObjectProcedure<CfhCategory>() {
            @Override
            public boolean execute(CfhCategory category) {
                CfhTopicsInitComposer.this.response.appendString(category.getName());
                CfhTopicsInitComposer.this.response.appendInt(category.getTopics().valueCollection().size());
                category.getTopics().forEachValue(new TObjectProcedure<CfhTopic>() {
                    @Override
                    public boolean execute(CfhTopic topic) {
                        CfhTopicsInitComposer.this.response.appendString(topic.name);
                        CfhTopicsInitComposer.this.response.appendInt(topic.id);
                        CfhTopicsInitComposer.this.response.appendString(topic.action.toString());
                        return true;
                    }
                });
                return true;
            }
        });

        return this.response;
    }
}