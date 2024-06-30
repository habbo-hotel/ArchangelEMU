package com.eu.habbo.roleplay.messages.outgoing.license;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.LicenseType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LicenseOpenComputerComposer extends MessageComposer {
    private final int itemID;
    private final int corpID;
    private final LicenseType licenseType;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.licenseOpenComputerComposer);
        this.response.appendInt(this.itemID);
        this.response.appendInt(this.corpID);
        this.response.appendInt(this.licenseType != null ? this.licenseType.getValue() : 0);
        return this.response;
    }

}
