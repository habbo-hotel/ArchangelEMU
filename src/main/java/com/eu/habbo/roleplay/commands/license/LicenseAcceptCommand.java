package com.eu.habbo.roleplay.commands.license;


import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class LicenseAcceptCommand extends Command {
    public LicenseAcceptCommand() {
        super("cmd_license_accept");
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
