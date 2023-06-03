package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.threading.runnables.UpdateModToolIssue;

public class ModToolSanctionEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().hasRight(Permission.ACC_SUPPORTTOOL)) {
            int ticketId = this.packet.readInt();
            int unknownInt = this.packet.readInt();
            int categoryId = this.packet.readInt();

            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(ticketId);

            if (issue != null) {
                issue.category = categoryId;
                new UpdateModToolIssue(issue).run();
                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
            }
        }
    }
}
