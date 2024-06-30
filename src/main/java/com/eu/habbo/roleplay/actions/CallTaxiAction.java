package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.roleplay.messages.outgoing.taxi.TaxiDispatchedComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import com.eu.habbo.roleplay.room.RoomType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CallTaxiAction implements Runnable {

    @NonNull
    private final Habbo habbo;

    @NonNull
    private final Room targetRoom;

    @Override
    public void run() {
        if (this.habbo.getRoomUnit().getRoom() == null) {
            return;
        }
        if (this.habbo.getRoomUnit().getRoom().getRoomInfo().getId() == this.targetRoom.getRoomInfo().getId() ) {
            return;
        }

        int taxiFee = Integer.parseInt(Emulator.getConfig().getValue("roleplay.taxi.fee", "20"));

        if (this.habbo.getHabboInfo().getCredits() < taxiFee) {
            this.habbo.whisper(Emulator.getTexts().getValue("roleplay.taxi.cant_afford"));
            return;
        }

        if (!this.targetRoom.getRoomInfo().getTags().contains(RoomType.TAXI)) {
            this.habbo.whisper(Emulator.getTexts()
                    .getValue("roleplay.taxi.not_available")
                    .replace(":roomname", this.targetRoom.getRoomInfo().getName())
            );
            return;
        }

        int taxiDelay = Integer.parseInt(Emulator.getConfig().getValue("roleplay.taxi.delay_secs", "20"));
        long arrivesAt = (System.currentTimeMillis() / 1000) + taxiDelay;

        this.habbo.getHabboInfo().setCredits(this.habbo.getHabboInfo().getCredits() - taxiFee);
        this.habbo.shout(
                Emulator.getTexts()
                        .getValue("roleplay.taxi.dispatched")
                        .replace(":roomName", this.targetRoom.getRoomInfo().getName())
                        .replace(":roomID", String.valueOf(this.targetRoom.getRoomInfo().getId()))
        );
        this.habbo.getClient().sendResponse(new CreditBalanceComposer(this.habbo));
        this.habbo.getClient().sendResponse(new UserRoleplayStatsChangeComposer(this.habbo));
        this.habbo.getClient().sendResponse(new TaxiDispatchedComposer(this.targetRoom.getRoomInfo().getId(), arrivesAt));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Set<Integer> scheduledSeconds = new HashSet<>();

        int countdownInterval = 5;
        for (int i = 1; i <= 4; i++) {
            int countdownSeconds = i * countdownInterval;

            if (countdownSeconds <= taxiDelay && !scheduledSeconds.contains(countdownSeconds)) {
                scheduledSeconds.add(countdownSeconds); // Mark countdownSeconds as scheduled

                int delaySeconds = taxiDelay - countdownSeconds; // Calculate delay

                executor.schedule(() -> {
                    this.habbo.getClient().getHabbo().shout(Emulator.getTexts()
                            .getValue("roleplay.taxi.eta")
                            .replace(":seconds", String.valueOf(countdownSeconds))
                    );
                }, delaySeconds, TimeUnit.SECONDS);
            }
        }

        executor.schedule(() -> {
            this.habbo.shout(Emulator.getTexts()
                    .getValue("roleplay.taxi.picked_up")
                    .replace(":fee", String.valueOf(taxiFee))
            );

            habbo.goToRoom(this.targetRoom.getRoomInfo().getId());

            executor.schedule(() -> {
                this.habbo.shout(Emulator.getTexts().getValue("roleplay.taxi.arrived"));
                executor.shutdown();
            }, 500, TimeUnit.MILLISECONDS);
        }, taxiDelay, TimeUnit.SECONDS);
    }

}
