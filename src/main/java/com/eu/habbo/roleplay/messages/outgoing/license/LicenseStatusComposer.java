package com.eu.habbo.roleplay.messages.outgoing.license;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.users.HabboLicense;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LicenseStatusComposer extends MessageComposer {
    private final LicenseType licenseType;
    private final HabboLicense habboLicense;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.licenseStatusComposer);
        this.response.appendInt(this.licenseType.getValue());
        this.response.appendBoolean(this.habboLicense != null);
        this.response.appendInt(this.habboLicense != null ? (int) this.habboLicense.getCreatedAt() : 0);
        return this.response;
    }
}
