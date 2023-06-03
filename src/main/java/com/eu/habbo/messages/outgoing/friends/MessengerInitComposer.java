package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.messenger.MessengerCategory;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MessengerInitComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {

        this.response.init(Outgoing.messengerInitComposer);
        if (this.habbo.hasRight(Permission.ACC_INFINITE_FRIENDS)) {
            this.response.appendInt(Integer.MAX_VALUE);
            this.response.appendInt(1337);
            this.response.appendInt(Integer.MAX_VALUE);
        } else {
            this.response.appendInt(Messenger.MAXIMUM_FRIENDS);
            this.response.appendInt(1337);
            this.response.appendInt(Messenger.MAXIMUM_FRIENDS_HC);
        }
        if (!this.habbo.getHabboInfo().getMessengerCategories().isEmpty()) {

            List<MessengerCategory> messengerCategories = this.habbo.getHabboInfo().getMessengerCategories();
            this.response.appendInt(messengerCategories.size());

            for (MessengerCategory mc : messengerCategories) {
                this.response.appendInt(mc.getId());
                this.response.appendString(mc.getName());
            }
        } else {
            this.response.appendInt(0);
        }
        return this.response;
    }
}

