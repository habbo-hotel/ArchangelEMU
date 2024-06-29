package com.eu.habbo.roleplay.commands.license;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.database.HabboLicenseRepository;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.license.LicenseMapper;
import com.eu.habbo.roleplay.messages.outgoing.license.LicenseStatusComposer;
import com.eu.habbo.roleplay.users.HabboLicense;

public class LicenseStatusCommand extends Command  {

    public LicenseStatusCommand() {
        super("cmd_license_lookup");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        if (params.length != 3) {
            return true;
        }

        int corpID = Integer.parseInt(params[1]);
        Corp licenseCorp = CorpManager.getInstance().getCorpByID(corpID);

        if (licenseCorp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.corp_not_found"));
            return true;
        }

        LicenseType licenseType = LicenseMapper.corpToLicenseType(licenseCorp);

        if (licenseType == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.license.corp_not_allowed"));
            return true;
        }

        String username = params[2];
        Habbo targetedHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

        if ( targetedHabbo == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("generic.user_not_found")
                    .replace(":username", username)
            );
            return true;
        }

        HabboLicense targetedLicense = HabboLicenseRepository.getInstance().getByUserAndLicense(targetedHabbo.getHabboInfo().getId(), licenseType);

        if (targetedLicense == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("roleplay.license.user_missing_license")
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
                    .replace(":license", licenseType.name())
            );
            return true;
        }

        gameClient.sendResponse(new LicenseStatusComposer(licenseType, targetedLicense));

        return true;
    }
}
