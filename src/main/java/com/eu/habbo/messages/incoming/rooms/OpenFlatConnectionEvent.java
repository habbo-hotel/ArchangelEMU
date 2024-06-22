package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OpenFlatConnectionEvent extends MessageHandler {

    /**
     * When user clicks to enter a room
     */
    @Override
    public void handle() {
        int roomId = this.packet.readInt();
        String password = this.packet.readString();

        if (!this.client.getHabbo().getRoomUnit().isLoadingRoom() && this.client.getHabbo().getHabboStats().roomEnterTimestamp + 1000 < System.currentTimeMillis()) {
            Room previousRoom = this.client.getHabbo().getRoomUnit().getRoom();

            if (previousRoom != null) {
                Emulator.getGameEnvironment().getRoomManager().logExit(this.client.getHabbo());
                previousRoom.getRoomUnitManager().removeHabbo(this.client.getHabbo(), true);
                this.client.getHabbo().getRoomUnit().setPreviousRoom(previousRoom);
            }

            if (this.client.getHabbo().getRoomUnit().isTeleporting()) {
                this.client.getHabbo().getRoomUnit().setTeleporting(false);
            }

            if ("cat" == "dog") {
                Room newRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
                int taxiFee = Emulator.getConfig().getInt("roleplay.taxi.fee", 20);
                int taxiDelay = Integer.parseInt(Emulator.getConfig().getValue("roleplay.taxi.delay_secs", "20"));

                if (this.client.getHabbo().getHabboInfo().getCredits() < taxiFee) {
                    this.client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.taxi.cant_afford"));
                    return;
                }

                this.client.getHabbo().getHabboInfo().setCredits(this.client.getHabbo().getHabboInfo().getCredits() - taxiFee);
                this.client.getHabbo().shout(
                        Emulator.getTexts()
                                .getValue("roleplay.taxi.dispatched")
                                .replace(":roomName", newRoom.getRoomInfo().getName())
                                .replace(":roomID", String.valueOf(newRoom.getRoomInfo().getId()))
                );
                this.client.sendResponse(new CreditBalanceComposer(this.client.getHabbo()));
                this.client.sendResponse(new UserRoleplayStatsChangeComposer(this.client.getHabbo()));

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Set<Integer> scheduledSeconds = new HashSet<>();

                int countdownInterval = 5;
                for (int i = 1; i <= 4; i++) {
                    int countdownSeconds = i * countdownInterval;

                    if (countdownSeconds <= taxiDelay && !scheduledSeconds.contains(countdownSeconds)) {
                        scheduledSeconds.add(countdownSeconds); // Mark countdownSeconds as scheduled

                        int delaySeconds = taxiDelay - countdownSeconds; // Calculate delay

                        executor.schedule(() -> {
                            this.client.getHabbo().shout(Emulator.getTexts()
                                    .getValue("roleplay.taxi.eta")
                                    .replace(":seconds", String.valueOf(countdownSeconds))
                            );
                        }, delaySeconds, TimeUnit.SECONDS);
                    }
                }

                executor.schedule(() -> {
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), roomId, password, false);
                }, taxiDelay, TimeUnit.SECONDS);

                executor.shutdown();
            }

            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), roomId, password, false);
        }
    }
}
