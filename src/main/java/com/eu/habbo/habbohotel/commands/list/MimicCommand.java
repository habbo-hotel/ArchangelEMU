package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.clothingvalidation.ClothingValidationManager;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.UserObjectComposer;
import com.eu.habbo.util.figure.FigureUtil;

public class MimicCommand extends Command {
    public MimicCommand() {
        super("cmd_mimic");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

            if (habbo == null) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_mimic.not_found"), ""), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (habbo == gameClient.getHabbo()) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_mimic.not_self"), RoomChatMessageBubbles.ALERT);
                return true;
            } else if (habbo.hasRight(Permission.ACC_NOT_MIMICED) && !gameClient.getHabbo().hasRight(Permission.ACC_NOT_MIMICED)) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_mimic.blocked"), params[1]).replace("%gender_name%", getGenderName(habbo)), RoomChatMessageBubbles.ALERT);
                return true;
            } else if (!habbo.hasRight(Permission.ACC_MIMIC_UNREDEEMED) && FigureUtil.hasBlacklistedClothing(habbo.getHabboInfo().getLook(), gameClient.getHabbo().getForbiddenClothing())) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_mimic.forbidden_clothing"), RoomChatMessageBubbles.ALERT);
                return true;
            } else {
                gameClient.getHabbo().getHabboInfo().setLook(ClothingValidationManager.VALIDATE_ON_MIMIC ? ClothingValidationManager.validateLook(gameClient.getHabbo(), habbo.getHabboInfo().getLook(), habbo.getHabboInfo().getGender().name()) : habbo.getHabboInfo().getLook());
                gameClient.getHabbo().getHabboInfo().setGender(habbo.getHabboInfo().getGender());
                gameClient.sendResponse(new UserObjectComposer(gameClient.getHabbo()));
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserChangeMessageComposer(gameClient.getHabbo()).compose());
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_mimic.copied"), params[1]).replace("%gender_name%", getGenderName(habbo)), RoomChatMessageBubbles.ALERT);
                return true;
            }
        } else {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_mimic.not_found"), ""), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }

    private String getGenderName(Habbo habbo) {
        return habbo.getHabboInfo().getGender().equals(HabboGender.M) ? getTextsValue("gender.him") : getTextsValue("gender.her");
    }
}
