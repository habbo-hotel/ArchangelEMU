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

            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), roomId, password, false);
        }
    }
}
