package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.*;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class CloseIssueDefaultActionEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().hasRight(Permission.ACC_SUPPORTTOOL)) {
            int issueId = this.packet.readInt();
            int unknown = this.packet.readInt();
            int category = this.packet.readInt();

            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(issueId);

            if (issue.modId == this.client.getHabbo().getHabboInfo().getId()) {
                CfhTopic modToolCategory = Emulator.getGameEnvironment().getModToolManager().getCfhTopic(category);

                if (modToolCategory != null) {
                    ModToolPreset defaultSanction = modToolCategory.getDefaultSanction();

                    if (defaultSanction != null) {
                        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(issue.reportedId);

                        if (defaultSanction.getBanLength() > 0) {
                            Emulator.getGameEnvironment().getModToolManager().ban(issue.reportedId, this.client.getHabbo(), defaultSanction.getMessage(), defaultSanction.getBanLength() * 86400, ModToolBanType.ACCOUNT, modToolCategory.getId());
                        } else if (defaultSanction.getMuteLength() > 0) {

                            if (target != null) {
                                target.mute(defaultSanction.getMuteLength() * 86400, false);
                            }
                        }
                    }
                }

                issue.state = ModToolTicketState.CLOSED;
                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
            } else {
                this.client.getHabbo().alert(Emulator.getTexts().getValue("supporttools.not_ticket_owner"));
            }
        }
    }
}
