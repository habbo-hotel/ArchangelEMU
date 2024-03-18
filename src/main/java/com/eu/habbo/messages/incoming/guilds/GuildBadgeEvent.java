package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.messages.incoming.MessageHandler;

public abstract class GuildBadgeEvent extends MessageHandler {
    protected StringBuilder createBadge(int count) {
        StringBuilder badge = new StringBuilder();

        byte base = 1;

        while (base < count) {
            int id = this.packet.readInt();
            int color = this.packet.readInt();
            int pos = this.packet.readInt();

            if (base == 1) {
                badge.append("b");
            } else {
                badge.append("s");
            }

            badge.append(id < 100 ? "0" : "").append(id < 10 ? "0" : "").append(id).append(color < 10 ? "0" : "").append(color).append(pos);

            base += 3;
        }
        return badge;
    }
}
