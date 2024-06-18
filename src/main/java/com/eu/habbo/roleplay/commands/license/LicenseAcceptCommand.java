package com.eu.habbo.roleplay.commands.license;


import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.RoleplayHelper;

public class LicenseAcceptCommand extends Command {
    public LicenseAcceptCommand() {
        super("cmd_license_offer");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (!LicenseHelper.getInstance().checkParams(gameClient, params)) {
            return true;
        }

        // Handle transaction


        return true;
    }
}
