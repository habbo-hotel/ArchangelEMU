package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnableCommand extends Command {
    public EnableCommand() {
        super("cmd_enable");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length >= 2) {
            int effectId;
            try {
                effectId = Integer.parseInt(params[1]);
            } catch (Exception e) {
                return false;
            }
            Habbo target = gameClient.getHabbo();
            if (params.length == 3) {
                target = getHabbo(params[2]);
            }

            if (target == null) {
                return true;
            }
            if (target == gameClient.getHabbo() || gameClient.getHabbo().hasRight(Permission.ACC_ENABLE_OTHERS)) {
                try {
                    if (target.getHabboInfo().getCurrentRoom() != null && target.getHabboInfo().getRiding() == null) {
                        if (Emulator.getGameEnvironment().getPermissionsManager().isEffectBlocked(effectId, target.getHabboInfo().getPermissionGroup().getId())) {
                            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_enable.not_allowed"), RoomChatMessageBubbles.ALERT);
                            return true;
                        }

                        target.getHabboInfo().getCurrentRoom().giveEffect(target, effectId, -1);
                    }
                } catch (Exception e) {
                    log.error("Caught exception", e);
                }
            }
        }
        return true;
    }
}
