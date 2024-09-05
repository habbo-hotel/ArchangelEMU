package com.eu.habbo.roleplay.messages.incoming.controls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.messages.outgoing.combat.CombatDelayComposer;
import com.eu.habbo.roleplay.messages.outgoing.combat.UserDiedComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import com.eu.habbo.roleplay.room.RoomType;
import com.eu.habbo.roleplay.users.HabboWeapon;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.weapons.WeaponType;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserAttackEvent extends MessageHandler {

    @Override
    public void handle() {
        int x = this.packet.readInt();
        int y = this.packet.readInt();
        int z = this.packet.readInt();

        RoomTile roomTile = this.client.getHabbo().getRoomUnit().getRoom().getLayout().getTile((short) x, (short) y);

        if (roomTile == null) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_hit_user_is_out_of_range").replace(":username", "target"));
            return;
        }

        if (this.client.getHabbo().getHabboRoleplayStats().getCombatBlocked()) {
            this.client.getHabbo().whisper("You need to wait a bit before attacking again.");
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            Runnable task = () -> {
                this.client.sendResponse(new CombatDelayComposer(this.client.getHabbo()));
                if (!this.client.getHabbo().getHabboRoleplayStats().getCombatBlocked()) {
                    executor.shutdown();
                }
            };
            executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        }


        if (this.client.getHabbo().getRoomUnit().getRoom().getRoomInfo().getTags().contains(RoomType.PASSIVE)) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.passive_room"));
            return;
        }

        int distanceX = x - this.client.getHabbo().getRoomUnit().getCurrentPosition().getX();
        int distanceY = y- this.client.getHabbo().getRoomUnit().getCurrentPosition().getY();

        HabboWeapon equippedWeapon = this.client.getHabbo().getInventory().getWeaponsComponent().getEquippedWeapon();


        if (equippedWeapon != null && equippedWeapon.getWeapon().getType() == WeaponType.GUN) {
            if (equippedWeapon.getCurrentAmmo() == 0) {
                this.client.getHabbo().whisper(equippedWeapon.getWeapon().getReloadMessage());
                this.client.getHabbo().getHabboRoleplayStats().setAttackTimeoutSeconds((int) equippedWeapon.getWeapon().getReloadTime());
                return;
            }
            equippedWeapon.setCurrentAmmo(equippedWeapon.getCurrentAmmo() - 1);
            this.client.sendResponse(new UserRoleplayStatsChangeComposer(this.client.getHabbo()));
        }

        int rangeInTiles = equippedWeapon != null ? equippedWeapon.getWeapon().getRangeInTiles() : 1;

        boolean isTargetWithinRange = Math.abs(distanceX) <= rangeInTiles && Math.abs(distanceY) <= rangeInTiles;

        int combatTimeout = equippedWeapon != null ? equippedWeapon.getWeapon().getCooldownSeconds() : Emulator.getConfig().getInt("roleplay.attack.delay_secs", 1);

        this.client.getHabbo().getHabboRoleplayStats().setAttackTimeoutSeconds(combatTimeout);

        Collection<Habbo> usersAtPosition = this.client.getHabbo().getRoomUnit().getRoom().getRoomUnitManager().getHabbosAt(roomTile);
        Habbo targetedHabbo = usersAtPosition.stream().findFirst().orElse(null);

        if (targetedHabbo == null) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.attack.target_missed"));
            return;
        }
        if (!isTargetWithinRange) {
            this.client.getHabbo().whisper(Emulator.getTexts()
                    .getValue("commands.roleplay.cmd_hit_user_is_out_of_range")
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
            );
            return;
        }

        int totalEnergy = Emulator.getConfig().getInt("roleplay.attack.energy", 8);

        if (totalEnergy > this.client.getHabbo().getHabboRoleplayStats().getEnergyNow()) {
            this.client.getHabbo().whisper(Emulator.getTexts()
                    .getValue("commands.roleplay.out_of_energy")
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
            );
            return;
        }

        int totalDamage = targetedHabbo.getHabboRoleplayStats().getDamageModifier(equippedWeapon);

        if (equippedWeapon != null) {
            String hitSuccessMessage = equippedWeapon.getWeapon().getAttackMessage()
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
                    .replace(":damage", Integer.toString(totalDamage))
                    .replace(":displayName", equippedWeapon.getWeapon().getDisplayName());
            this.client.getHabbo().shout(hitSuccessMessage);
            this.client.getHabbo().getHabboRoleplayStats().addWeaponXP(totalDamage);
        } else {
            String hitSuccessMessage = Emulator.getTexts()
                    .getValue("commands.roleplay.cmd_hit_success")
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
                    .replace(":damage", Integer.toString(totalDamage));
            this.client.getHabbo().shout(hitSuccessMessage);
            this.client.getHabbo().getHabboRoleplayStats().addMeleeXP(totalDamage);
        }

        targetedHabbo.getHabboRoleplayStats().setHealth(targetedHabbo.getHabboRoleplayStats().getHealthNow() - totalDamage);

        targetedHabbo.shout(Emulator.getTexts()
                .getValue("commands.roleplay.user_health_remaining")
                .replace(":currentHealth", Integer.toString(targetedHabbo.getHabboRoleplayStats().getHealthNow()))
                .replace(":maximumHealth", Integer.toString(targetedHabbo.getHabboRoleplayStats().getHealthMax()))
        );

        this.client.getHabbo().getHabboRoleplayStats().depleteEnergy(totalEnergy);
        targetedHabbo.shout(Emulator.getTexts()
                .getValue("commands.roleplay.user_energy_remaining")
                .replace(":energyNow", Integer.toString(this.client.getHabbo().getHabboRoleplayStats().getEnergyNow()))
                .replace(":energyMax", Integer.toString(this.client.getHabbo().getHabboRoleplayStats().getEnergyMax()))
        );

        if (targetedHabbo.getHabboRoleplayStats().isDead()) {
            Collection<Habbo> onlineHabbos = Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values();

            for (Habbo onlineHabbo : onlineHabbos) {
                onlineHabbo.getClient().sendResponse(new UserDiedComposer(targetedHabbo, this.client.getHabbo()));
            }
        }
    }
}
