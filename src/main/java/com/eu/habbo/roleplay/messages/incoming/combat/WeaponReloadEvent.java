package com.eu.habbo.roleplay.messages.incoming.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import com.eu.habbo.roleplay.users.HabboWeapon;

public class WeaponReloadEvent extends MessageHandler {
    @Override
    public void handle() {
        HabboWeapon weapon = this.client.getHabbo().getInventory().getWeaponsComponent().getEquippedWeapon();

        if (weapon == null) {
            return;
        }

        if (weapon.getCurrentAmmo() >= weapon.getWeapon().getAmmoCapacity()) {
            return;
        }

        weapon.setCurrentAmmo(0);
        this.client.sendResponse(new UserRoleplayStatsChangeComposer(this.client.getHabbo()));

        Emulator.getThreading().run(() -> {
            this.client.getHabbo().shout(weapon.getWeapon().getReloadMessage());
            weapon.setCurrentAmmo(weapon.getWeapon().getAmmoCapacity());
            this.client.sendResponse(new UserRoleplayStatsChangeComposer(this.client.getHabbo()));
        }, (int) weapon.getWeapon().getReloadTime());


    }
}