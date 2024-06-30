package com.eu.habbo.roleplay.commands.taxi;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.roleplay.actions.CallTaxiAction;
import com.eu.habbo.roleplay.messages.outgoing.taxi.TaxiDispatchedComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import com.eu.habbo.roleplay.room.RoomType;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CallTaxiCommand extends Command {
    public CallTaxiCommand() {
        super("cmd_taxi");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return false;
        }

        if (params[1] == null) {
            return true;
        }

        int roomID = Integer.parseInt(params[1]);

        if (gameClient.getHabbo().getRoomUnit().getRoom() != null && gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() == roomID ) {
            return true;
        }

        Room targetedRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(roomID);

        if (targetedRoom == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.taxi.not_found"));
            return true;
        }

        Emulator.getThreading().run(new CallTaxiAction(gameClient.getHabbo(), targetedRoom));

        return true;
    }
}