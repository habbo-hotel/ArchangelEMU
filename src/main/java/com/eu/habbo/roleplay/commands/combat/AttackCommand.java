package com.eu.habbo.roleplay.commands.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.RoleplayHelper;
import com.eu.habbo.roleplay.messages.outgoing.combat.CombatDelayComposer;
import com.eu.habbo.roleplay.room.RoomType;
import com.eu.habbo.roleplay.users.HabboWeapon;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AttackCommand extends Command {
    public AttackCommand() {
        super("cmd_attack");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getCombatBlocked()) {
            gameClient.getHabbo().whisper("You need to wait a bit before attacking again.");
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            Runnable task = () -> {
                gameClient.sendResponse(new CombatDelayComposer(gameClient.getHabbo()));
                if (!gameClient.getHabbo().getHabboRoleplayStats().getCombatBlocked()) {
                    executor.shutdown();
                }
            };
            executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        }

        gameClient.getHabbo().getHabboRoleplayStats().setLastAttackTime(System.currentTimeMillis());

        Habbo targetedHabbo = RoleplayHelper.getInstance().getTarget(gameClient, params);

        if (targetedHabbo == null) {
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getTags().contains(RoomType.PASSIVE)) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.passive_room"));
            return true;
        }

        int distanceX = targetedHabbo.getRoomUnit().getCurrentPosition().getX() - gameClient.getHabbo().getRoomUnit().getCurrentPosition().getX();
        int distanceY = targetedHabbo.getRoomUnit().getCurrentPosition().getY() - gameClient.getHabbo().getRoomUnit().getCurrentPosition().getY();

        HabboWeapon equippedWeapon = gameClient.getHabbo().getInventory().getWeaponsComponent().getEquippedWeapon();

        int rangeInTiles = equippedWeapon != null ? equippedWeapon.getWeapon().getRangeInTiles() : 1;

        boolean isTargetWithinRange = Math.abs(distanceX) <= rangeInTiles && Math.abs(distanceY) <= rangeInTiles;

        if (!isTargetWithinRange) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_hit_user_is_out_of_range").replace(":username", targetedHabbo.getHabboInfo().getUsername()));
            return true;
        }

        int totalDamage = targetedHabbo.getHabboRoleplayStats().getDamageModifier();

        if (equippedWeapon != null) {
            String hitSuccessMessage = equippedWeapon.getWeapon().getAttackMessage()
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
                    .replace(":damage", Integer.toString(totalDamage))
                    .replace(":displayName", equippedWeapon.getWeapon().getDisplayName());
            gameClient.getHabbo().shout(hitSuccessMessage);
        }

        if (equippedWeapon == null) {
            String hitSuccessMessage = Emulator.getTexts()
                    .getValue("commands.roleplay.cmd_hit_success")
                    .replace(":username", targetedHabbo.getHabboInfo().getUsername())
                    .replace(":damage", Integer.toString(totalDamage));
            gameClient.getHabbo().shout(hitSuccessMessage);
        }

        targetedHabbo.getHabboRoleplayStats().setHealth(targetedHabbo.getHabboRoleplayStats().getHealthNow() - totalDamage);

        targetedHabbo.shout(Emulator.getTexts()
                .getValue("commands.roleplay.user_health_remaining")
                .replace(":currentHealth", Integer.toString(targetedHabbo.getHabboRoleplayStats().getHealthNow()))
                .replace(":maximumHealth", Integer.toString(targetedHabbo.getHabboRoleplayStats().getHealthMax()))
        );

        return true;
    }
}