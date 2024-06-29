package com.eu.habbo.roleplay.facility.prison;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.roleplay.interactions.InteractionPrisonBench;
import com.eu.habbo.roleplay.room.RoomType;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class FacilityPrisonManager {
    
    private static FacilityPrisonManager instance;

    public static FacilityPrisonManager getInstance() {
        if (instance == null) {
            instance = new FacilityPrisonManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityPrisonManager.class);

    private FacilityPrisonManager() {
        long millis = System.currentTimeMillis();
        this.usersInJail = new TIntObjectHashMap<>();
        LOGGER.info("Prison Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    private final TIntObjectHashMap<PrisonSentence> usersInJail;

    public Room getNearestPrison() {
        List<Room> prisonRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(RoomType.PRISON);

        if (prisonRooms.isEmpty()) {
            FacilityPrisonManager.LOGGER.error("No prison rooms found");
            throw new RuntimeException("No prison rooms found");
        }

        return prisonRooms.get(0);
    }

    public PrisonSentence getPrisonTime(Habbo user) {
        if (this.usersInJail.isEmpty()) {
            return null;
        }
        return this.usersInJail.get(user.getHabboInfo().getId());
    }

    public void addPrisonTime(Habbo habbo, String crime, int timeLeft) {
        Room room = this.getNearestPrison();

        if (habbo.getRoomUnit().getRoom().getRoomInfo().getId() != room.getRoomInfo().getId()) {
            habbo.shout(Emulator.getTexts()
                    .getValue("roleplay.prison.teleport")
                    .replace(":roomName", room.getRoomInfo().getName())
            );
            habbo.goToRoom(room.getRoomInfo().getId());
        }

        Collection<RoomItem> prisonBenches = room.getRoomItemManager().getItemsOfType(InteractionPrisonBench.class);
        for (RoomItem hospitalBedItem : prisonBenches) {
            List<RoomTile> prisonBenchTiles = hospitalBedItem.getOccupyingTiles(room.getLayout());
            RoomTile firstAvailablePrisonBenchTile = prisonBenchTiles.get(0);
            if (firstAvailablePrisonBenchTile == null) {
                return;
            }
            habbo.getRoomUnit().setLocation(firstAvailablePrisonBenchTile);
        }

        habbo.getHabboInfo().setMotto(Emulator.getTexts().getValue("roleplay.prison.activity").replace(":timeLeft", String.valueOf(timeLeft)));
        habbo.shout(Emulator.getTexts().getValue("roleplay.prison.starts_sentence").replace(":timeLeft", Integer.toString(timeLeft)).replace(":crime", crime));
        this.usersInJail.put(habbo.getHabboInfo().getId(), new PrisonSentence(habbo, crime, timeLeft, 0));
    }

    public void removePrisonTime(Habbo user) {
        if (this.getPrisonTime(user) == null) {
            return;
        }
        this.usersInJail.remove(user.getHabboInfo().getId());
        user.getRoomUnit().setRoom(user.getRoomUnit().getRoom());
        user.shout(Emulator.getTexts().getValue("roleplay.prison.finishes_sentence"));
    }

    public void cycle() {
        TIntObjectIterator<PrisonSentence> iterator = usersInJail.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            PrisonSentence sentence = iterator.value();
            if (sentence.getTimeLeft() == 0) {
                this.removePrisonTime(sentence.getHabbo());
                return;
            }
            sentence.getHabbo().getHabboInfo().setMotto(Emulator.getTexts().getValue("roleplay.prison.activity").replace(":timeLeft", String.valueOf(sentence.getTimeLeft())));
            sentence.getHabbo().getHabboInfo().run();
            sentence.getHabbo().getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(sentence.getHabbo()).compose());
        }
    }

    public void dispose() {
    }
}