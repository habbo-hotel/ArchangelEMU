package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.RoomUserPetComposer;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.PetData;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.messages.outgoing.generic.alerts.MOTDNotificationComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;

import java.util.ArrayList;
import java.util.Collections;

public class TransformCommand extends Command {
    public TransformCommand() {
        super("cmd_transform");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1) {
            StringBuilder petNames = new StringBuilder();
            petNames.append(getTextsValue("commands.generic.cmd_transform.title"));
            petNames.append("\r------------------------------------------------------------------------------\r");
            ArrayList<PetData> petData = new ArrayList<>(Emulator.getGameEnvironment().getPetManager().getPetData());
            Collections.sort(petData);
            String line = getTextsValue("commands.generic.cmd_transform.line");
            for (PetData p : petData) {
                petNames.append(line.replace("%id%", p.getType() + "").replace("%name%", p.getName())).append("\r");
            }

            gameClient.sendResponse(new MOTDNotificationComposer(new String[]{petNames.toString()}));
        } else {
            String petName = params[1];
            PetData petData = Emulator.getGameEnvironment().getPetManager().getPetData(petName);

            int race = 0;

            if (params.length >= 3) {
                try {
                    race = Integer.parseInt(params[2]);
                } catch (Exception e) {
                    return true;
                }
            }

            String color = "FFFFFF";
            if (params.length >= 4) {
                color = params[3];
            }

            if (petData != null) {
                RoomUnit roomUnit = gameClient.getHabbo().getRoomUnit();
                roomUnit.setRoomUnitType(RoomUnitType.PET);
                gameClient.getHabbo().getHabboStats().getCache().put("pet_type", petData);
                gameClient.getHabbo().getHabboStats().getCache().put("pet_race", race);
                gameClient.getHabbo().getHabboStats().getCache().put("pet_color", color);
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserRemoveMessageComposer(gameClient.getHabbo().getRoomUnit()).compose());
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserPetComposer(petData.getType(), race, color, gameClient.getHabbo()).compose());
            } else {
                //Pet Not Found
                return true;
            }
        }
        return true;
    }
}