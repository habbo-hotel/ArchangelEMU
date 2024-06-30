package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import com.eu.habbo.roleplay.interactions.InteractionPrisonBench;
import com.eu.habbo.roleplay.room.RoomType;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServeJailTimeAction implements Runnable {

    private final Habbo habbo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> cycle;
    private ScheduledFuture<?> checkState;

    public String crime;
    private String oldMotto;
    private String oldFigure;

    private final long endsAt;

    public ServeJailTimeAction(Habbo habbo, String crime, int minutesLeft) {
        this.habbo = habbo;
        this.crime = crime;
        this.endsAt = Instant.now().getEpochSecond() + Duration.ofMinutes(minutesLeft).getSeconds();
        this.habbo.getHabboRoleplayStats().setJailed(true);
    }

    @Override
    public void run() {
        this.oldMotto = habbo.getHabboInfo().getMotto();
        this.oldFigure = habbo.getHabboInfo().getLook();

        cycle = scheduler.scheduleAtFixedRate(this::cycleSentence, 0, 60, TimeUnit.SECONDS);
        checkState = scheduler.scheduleAtFixedRate(this::checkState, 0, 1, TimeUnit.SECONDS);

        List<Room> prisonRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsByTag(RoomType.PRISON);

        if (prisonRooms.isEmpty()) {
            throw new RuntimeException("no prisons found");
        }

        Room room = prisonRooms.get(0);

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


        habbo.shout(Emulator.getTexts()
                .getValue("roleplay.jail.sentence_start")
                .replace(":crime", this.crime)
        );

        habbo.getHabboInfo().changeClothes(habbo.getHabboInfo().getGender() == HabboGender.M
                ? Emulator.getConfig().getValue("roleplay.prison.male_uniform")
                : Emulator.getConfig().getValue("roleplay.prison.female_uniform")
        );

        habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
        habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo.getClient().getHabbo()).compose());
    }

    private void checkState() {
        if (!habbo.getHabboRoleplayStats().isJailed()) {
            habbo.shout(Emulator.getTexts().getValue("roleplay.jail.bail"));
            cycle.cancel(true);
            checkState.cancel(true);
        }
    }

    private void cycleSentence() {
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        int secondsLeft = (int) (this.endsAt - currentTimeInSeconds);
        int minutesLeft = secondsLeft / 60;

        habbo.getHabboInfo().setMotto(Emulator.getTexts().
                getValue("roleplay.jail.sentence_motto")
                .replace(":id", String.valueOf(this.habbo.getHabboInfo().getId() * 3))
                .replace(":time", String.valueOf(minutesLeft > 0 ? minutesLeft : secondsLeft))
                .replace(":unit", minutesLeft > 0 ? "minutes" : "seconds")
                .replace(":crime", this.crime)
        );

        if (secondsLeft <= 0) {
            this.onFinishSentence();
            return;
        }

        habbo.shout(Emulator.getTexts()
                .getValue("roleplay.jail.sentence_progress")
                .replace(":time", String.valueOf(minutesLeft > 0 ? minutesLeft : secondsLeft))
                .replace(":unit", minutesLeft > 0 ? "minutes" : "seconds")
        );
    }

    private void onFinishSentence() {
        this.habbo.getHabboRoleplayStats().setJailed(true);
        habbo.shout(Emulator.getTexts().getValue("roleplay.jail.sentence_finish"));
        habbo.getHabboRoleplayStats().setJailed(false);
        habbo.getHabboInfo().setMotto(this.oldMotto);
        habbo.getHabboInfo().changeClothes(this.oldFigure);
        habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
        habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo.getClient().getHabbo()).compose());
    }
}

