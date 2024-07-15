package com.eu.habbo.roleplay.messages.outgoing.police;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.police.PoliceReport;
import com.eu.habbo.roleplay.police.PoliceReportManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PoliceCallInfoComposer extends MessageComposer {
    public final PoliceReport policeReport;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.policeCallReceivedComposer);
        this.response.appendInt(this.policeReport.getReportingUser().getHabboInfo().getId());
        this.response.appendString(this.policeReport.getReportingUser().getHabboInfo().getUsername());
        this.response.appendString(this.policeReport.getReportingUser().getHabboInfo().getLook());
        this.response.appendInt(this.policeReport.getReportingUser().getRoomUnit().getRoom().getRoomInfo().getId());
        this.response.appendString(this.policeReport.getMessage());
        this.response.appendInt(PoliceReportManager.getInstance().getIndexByPoliceReport(this.policeReport));
        this.response.appendInt(PoliceReportManager.getInstance().getMaxPoliceReportIndex());

        return this.response;
    }
}
