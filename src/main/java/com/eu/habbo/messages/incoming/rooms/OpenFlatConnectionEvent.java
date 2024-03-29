package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corps.CorporationsShiftManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenFlatConnectionEvent extends MessageHandler {

    /**
     * When user clicks to enter a room
     */
    @Override
    public void handle() {
        int roomId = this.packet.readInt();
        String password = this.packet.readString();

        // TODO: Redo later on and make it prevent leaving when dead and in hospital instead of redirecting back
        if (this.client.getHabbo().getHabboRoleplayStats().isDead()) {
            Room previousRoom = this.client.getHabbo().getRoomUnit().getRoom();
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), previousRoom.getRoomInfo().getId(), password, true);
            return;
        }

        if (CorporationsShiftManager.getInstance().isUserWorking(this.client.getHabbo())) {
            CorporationsShiftManager.getInstance().stopUserShift(this.client.getHabbo(), false, false);
        }

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
