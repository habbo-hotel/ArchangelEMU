package com.eu.habbo.roleplay.commands.license;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.billing.UserBill;
import com.eu.habbo.roleplay.billing.items.*;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.database.HabboBillRepository;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.messages.outgoing.billing.InvoiceReceivedComposer;

public class LicenseOfferCommand extends Command {

    public LicenseOfferCommand() {
        super("cmd_license_offer");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null || params.length == 0) {
            return true;
        }

        String targetedUsername = params[1];

        if (gameClient.getHabbo().getHabboRoleplayStats().isStunned() || gameClient.getHabbo().getHabboRoleplayStats().isCuffed() || gameClient.getHabbo().getHabboRoleplayStats().getEscortedBy() != null || gameClient.getHabbo().getHabboRoleplayStats().isDead()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_allowed"));
            return true;
        }

        if (targetedUsername == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.user_not_found"));
            return true;
        }

        Habbo targetedHabbo = gameClient.getHabbo().getRoomUnit().getRoom().getRoomUnitManager().getRoomHabboByUsername(targetedUsername);

        if (targetedHabbo == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.user_not_found").replace("%username%", targetedUsername));
            return true;
        }

        if (targetedHabbo.getHabboRoleplayStats().isDead()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.target_dead").replace(":username", targetedUsername));
            return true;
        }

        if (params[2] == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_not_found"));
            return true;
        }

        LicenseType licenseType = LicenseType.fromValue(Integer.parseInt(params[2]));

        UserBill statement = null;

        if (targetedHabbo.getInventory().getLicensesComponent().getLicenseByType(licenseType) != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_already_exists"));
            return true;
        }

        if (licenseType == LicenseType.DRIVER) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.DRIVING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
            statement = HabboBillRepository.getInstance().create(new DriverLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));
        }

        if (licenseType == LicenseType.FARMING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.FARMING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
            statement = HabboBillRepository.getInstance().create(new FarmingLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));
        }

        if (licenseType == LicenseType.FISHING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.FISHING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
            statement = HabboBillRepository.getInstance().create(new FishingLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));
        }

        if (licenseType == LicenseType.MINING) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.MINING_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed"));
                return true;
            }
            statement = HabboBillRepository.getInstance().create(new MiningLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));
        }

        if (licenseType == LicenseType.WEAPON) {
            if (!gameClient.getHabbo().getHabboRoleplayStats().getCorp().getTags().contains(CorpTag.WEAPONS_AUTHORITY)) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.license_sell_not_allowed").replace(":license", licenseType.name()));
                return true;
            }
            statement = HabboBillRepository.getInstance().create(new WeaponLicenseBillingItem(targetedHabbo.getHabboInfo().getId(), gameClient.getHabbo().getHabboInfo().getId()));
        }

        if (statement == null) {;
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.error"));
            return true;
        }

        targetedHabbo.getClient().sendResponse(new InvoiceReceivedComposer(statement));
        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.license_offered").replace(":license", licenseType.name()).replace(":username", targetedHabbo.getHabboInfo().getUsername()));

        return true;
    }
}
