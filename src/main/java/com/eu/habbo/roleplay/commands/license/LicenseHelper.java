package com.eu.habbo.roleplay.commands.license;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.RoleplayHelper;
import com.eu.habbo.roleplay.government.LicenseType;

public class LicenseHelper {

    private static LicenseHelper instance;

    public static LicenseHelper getInstance() {
        if (instance == null) {
            instance = new LicenseHelper();
        }
        return instance;
    }

    public boolean checkParams(GameClient gameClient, String[] params) {
        if (params == null) {
            return false;
        }

        Habbo targetedHabbo = RoleplayHelper.getInstance().getTarget(gameClient, params);

        if (targetedHabbo == null) {
            return false;
        }

        String license = params[2];

        if (license == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_not_found"));
            return false;
        }

        LicenseType licenseType = LicenseType.fromValue(Integer.parseInt(license));

        if (targetedHabbo.getInventory().getLicensesComponent().getLicenseByType(licenseType) != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_already_exists"));
            return false;
        }

        return true;
    }
}
