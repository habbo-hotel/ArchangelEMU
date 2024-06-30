package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.LicenseType;
import com.eu.habbo.roleplay.corp.LicenseMapper;
import com.eu.habbo.roleplay.messages.outgoing.license.LicenseOpenComputerComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionLicenseComputer extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_license_pc";

    public InteractionLicenseComputer(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionLicenseComputer(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        int corpID = Integer.parseInt(this.getExtraData());
        Corp licenseAgency = CorpManager.getInstance().getCorpByID(corpID);
        LicenseType licenseType = licenseAgency != null ? LicenseMapper.corpToLicenseType(licenseAgency) : null;
        if (licenseAgency == null) {
            if (!client.getHabbo().getHabboRoleplayStats().isWorking()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
                return;
            }

            if (this.getOwnerInfo().getId() != client.getHabbo().getHabboInfo().getId()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.device.not_setup"));
                return;
            }


            client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.device.set_up"));
            client.sendResponse(new LicenseOpenComputerComposer(this.getId(), corpID, null));
            return;
        }

        client.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.computer.logged_in")
                .replace(":corpName", licenseAgency.getGuild().getName())
        );

        client.sendResponse(new LicenseOpenComputerComposer(this.getId(), corpID, licenseType));
    }
}