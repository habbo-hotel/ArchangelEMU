package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.ResolveReportCommand;

public class PoliceResolveReportEvent extends MessageHandler {
    @Override
    public void handle() {
        String reportId = String.valueOf(this.packet.readInt());
        String flagged = this.packet.readBoolean() ? "true" : "false";

        if (reportId == null) {
            return;
        }

        new ResolveReportCommand().handle(this.client,new String[] {null, reportId, flagged});
    }
}