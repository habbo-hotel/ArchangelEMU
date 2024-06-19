package com.eu.habbo.roleplay.commands.license;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.RoleplayHelper;
import com.eu.habbo.roleplay.billing.BillingStatement;
import com.eu.habbo.roleplay.billing.items.WeaponLicenseBillingItem;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.messages.outgoing.billing.InvoiceReceivedComposer;

public class LicenseOfferCommand extends Command {

    public LicenseOfferCommand() {
        super("cmd_license_offer");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        Habbo targetedHabbo = RoleplayHelper.getInstance().getTarget(gameClient, params);

        if (targetedHabbo == null) {
            return true;
        }

        if (params[2] == null) {

            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_not_found"));
            return true;
        }

        LicenseType licenseType = LicenseType.fromValue(Integer.parseInt(params[2]));

        if (targetedHabbo.getInventory().getLicensesComponent().getLicenseByType(licenseType) != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_already_exists"));
            return true;
        }

        if (licenseType == LicenseType.DRIVER) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.DRIVING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
        }

        if (licenseType == LicenseType.FARMING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.FARMING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
        }

        if (licenseType == LicenseType.FISHING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.FISHING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
        }

        if (licenseType == LicenseType.MINING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.MINING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
        }

        if (licenseType == LicenseType.WEAPON) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.WEAPONS_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed").replace(":license", licenseType.name()));
                return true;
            }
        }

        BillingStatement statement = BillingStatement.create(new WeaponLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));

        targetedHabbo.getClient().sendResponse(new InvoiceReceivedComposer(statement));

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.license_offered").replace(":license", licenseType.name()).replace(":username", targetedHabbo.getHabboInfo().getUsername()));

        return true;
    }
}
