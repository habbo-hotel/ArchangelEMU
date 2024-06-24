package com.eu.habbo.roleplay.commands.taxi;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
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

        int taxiFee = Integer.parseInt(Emulator.getConfig().getValue("roleplay.taxi.fee", "20"));

        if (gameClient.getHabbo().getHabboInfo().getCredits() < taxiFee) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.taxi.cant_afford"));
            return true;
        }

        if (!targetedRoom.getRoomInfo().getTags().contains(RoomType.TAXI)) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("roleplay.taxi.not_available")
                    .replace(":roomname", targetedRoom.getRoomInfo().getName())
            );
            return true;
        }

        int taxiDelay = Integer.parseInt(Emulator.getConfig().getValue("roleplay.taxi.delay_secs", "20"));
        long arrivesAt = (System.currentTimeMillis() / 1000) + taxiDelay;

        gameClient.getHabbo().getHabboInfo().setCredits(gameClient.getHabbo().getHabboInfo().getCredits() - taxiFee);
        gameClient.getHabbo().shout(
                Emulator.getTexts()
                        .getValue("roleplay.taxi.dispatched")
                        .replace(":roomName", targetedRoom.getRoomInfo().getName())
                        .replace(":roomID", String.valueOf(targetedRoom.getRoomInfo().getId()))
        );
        gameClient.sendResponse(new CreditBalanceComposer(gameClient.getHabbo()));
        gameClient.sendResponse(new UserRoleplayStatsChangeComposer(gameClient.getHabbo()));
        gameClient.sendResponse(new TaxiDispatchedComposer(targetedRoom.getRoomInfo().getId(), arrivesAt));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Set<Integer> scheduledSeconds = new HashSet<>();

        int countdownInterval = 5;
        for (int i = 1; i <= 4; i++) {
            int countdownSeconds = i * countdownInterval;

            if (countdownSeconds <= taxiDelay && !scheduledSeconds.contains(countdownSeconds)) {
                scheduledSeconds.add(countdownSeconds); // Mark countdownSeconds as scheduled

                int delaySeconds = taxiDelay - countdownSeconds; // Calculate delay

                executor.schedule(() -> {
                    gameClient.getHabbo().shout(Emulator.getTexts()
                            .getValue("roleplay.taxi.eta")
                            .replace(":seconds", String.valueOf(countdownSeconds))
                    );
                }, delaySeconds, TimeUnit.SECONDS);
            }
        }

        executor.schedule(() -> {
            gameClient.getHabbo().shout(Emulator.getTexts()
                    .getValue("roleplay.taxi.picked_up")
                    .replace(":fee", String.valueOf(taxiFee))
            );
            Emulator.getGameEnvironment().getRoomManager().enterRoom(gameClient.getHabbo(), targetedRoom.getRoomInfo().getId(), targetedRoom.getRoomInfo().getPassword(), false);
            executor.schedule(() -> {
                gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.taxi.arrived"));
                executor.shutdown();
                }, 1500, TimeUnit.MILLISECONDS);
        }, taxiDelay, TimeUnit.SECONDS);
        return true;
    }
}