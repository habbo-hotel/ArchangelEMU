package com.eu.habbo.messages.outgoing.habboway.nux;

import com.eu.habbo.habbohotel.items.NewUserGift;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class NewUserExperienceGiftOfferComposer extends MessageComposer {
    private final List<List<NewUserGift>> options;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.newUserExperienceGiftOfferComposer);
        this.response.appendInt(this.options.size());
        for (List<NewUserGift> option : this.options) {
            this.response.appendInt(1);
            this.response.appendInt(3);
            this.response.appendInt(option.size());
            for (NewUserGift gift : option) {
                gift.serialize(this.response);
            }
        }
        return this.response;
    }
}