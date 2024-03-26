package com.eu.habbo.roleplay.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.users.HabboWeapon;


public class EquipCommand extends Command {
    public EquipCommand() {
        super("cmd_equip");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().getInventory().getWeaponsComponent().setEquippedWeapon(null);
            gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_equip_weapon_unequipped"));
            return true;
        }

        String weaponUniqueName = params[1];

        if (weaponUniqueName == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_equip_no_weapon_specified"));
            return true;
        }

        HabboWeapon matchingWeapon = gameClient.getHabbo().getInventory().getWeaponsComponent().getWeaponByUniqueName(weaponUniqueName);

        if (matchingWeapon == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_equip_weapon_not_found").replace("%uniqueName%", weaponUniqueName));
            return true;
        }

        gameClient.getHabbo().getInventory().getWeaponsComponent().setEquippedWeapon(matchingWeapon);
        gameClient.getHabbo().shout(matchingWeapon.getWeapon().getEquipMessage());

        return true;
    }
}
