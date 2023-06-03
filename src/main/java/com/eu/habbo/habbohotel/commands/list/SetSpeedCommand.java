package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class SetSpeedCommand extends Command {
    public SetSpeedCommand() {
        super("cmd_set_speed");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo())) {
            Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

            int oldSpeed = room.getRollerSpeed();
            int newSpeed;

            try {
                newSpeed = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_setspeed.invalid_amount"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (newSpeed < -1 || newSpeed > Emulator.getConfig().getInt("hotel.rollers.speed.maximum")) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_setspeed.bounds"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            room.setRollerSpeed(newSpeed);

            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_setspeed").replace("%oldspeed%", oldSpeed + "").replace("%newspeed%", newSpeed + ""), RoomChatMessageBubbles.ALERT);
            return true;
        }
        return false;
    }
}
