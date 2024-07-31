package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TurfContextMenuInfoComposer extends MessageComposer {
        private final Guild guild;
        private final RoomItem item;

        @Override
        protected ServerMessage composeInternal() {
            this.response.init(Outgoing.turfContextMenuInfoComposer);
            this.response.appendInt(item.getId());
            this.response.appendInt(this.guild  != null ? this.guild.getId() : -1);
            this.response.appendString(this.guild != null ? this.guild.getName() : "");
            return this.response;
        }
    }
