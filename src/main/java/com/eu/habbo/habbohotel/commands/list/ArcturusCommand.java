package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class ArcturusCommand extends Command {
    public ArcturusCommand() {
        super("cmd_arcturus");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            gameClient.getHabbo().whisper("This hotel is powered by Arcturus Emulator! \r" +
                            "Cet hôtel est alimenté par Arcturus émulateur! \r" +
                            "Dit hotel draait op Arcturus Emulator! \r" +
                            "Este hotel está propulsado por Arcturus emulador! \r" +
                            "Hotellet drivs av Arcturus Emulator! \r" +
                            "Das Hotel gehört zu Arcturus Emulator betrieben!"
                    , RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
