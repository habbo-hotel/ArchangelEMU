package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpPosition;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WorkShiftAction implements Runnable {
    private final Habbo habbo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> cycleUserShift;
    private ScheduledFuture<?> checkWorkingState;

    private String oldMotto;
    private String oldFigure;

    public WorkShiftAction(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    public void run() {
        this.oldMotto = habbo.getHabboInfo().getMotto();
        this.oldFigure = habbo.getHabboInfo().getLook();
        Corp corp = habbo.getHabboRoleplayStats().getCorp();
        CorpPosition corpPosition = habbo.getHabboRoleplayStats().getCorpPosition();
        long endsAt = Instant.now().getEpochSecond() + Duration.ofMinutes(10).getSeconds();

        cycleUserShift = scheduler.scheduleAtFixedRate(() -> cycleUserShift(habbo, corpPosition, endsAt), 0, 60, TimeUnit.SECONDS);
        checkWorkingState = scheduler.scheduleAtFixedRate(() -> checkWorkingState(habbo, corpPosition), 0, 1, TimeUnit.SECONDS);

        habbo.shout(Emulator.getTexts().getValue("roleplay.shift.start").replace(":position", corpPosition.getName()));
        habbo.getHabboRoleplayStats().setWorking(true);
        habbo.getHabboInfo().setMotto(corpPosition.getActivity());
        habbo.getHabboInfo().changeClothes(habbo.getHabboInfo().getGender() == HabboGender.M ? corpPosition.getMaleFigure() : corpPosition.getFemaleFigure());
        habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
        habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo.getClient().getHabbo()).compose());
    }

    private void checkWorkingState(Habbo habbo, CorpPosition corpPosition) {
        if (!habbo.getHabboRoleplayStats().isWorking()) {
            habbo.shout(Emulator.getTexts().getValue("roleplay.shift.cancel"));
            cycleUserShift.cancel(true);
            checkWorkingState.cancel(true);
        }
    }

    private void cycleUserShift(Habbo habbo, CorpPosition corpPosition, long endsAt) {
        if (habbo.getRoomUnit().getRoom() == null) {
            return;
        }

        Corp corp = habbo.getHabboRoleplayStats().getCorp();

        if (habbo.getRoomUnit().getRoom().getRoomInfo().getId() != corp.getGuild().getRoomId() && !corpPosition.isCanWorkAnywhere()) {
            habbo.shout(Emulator.getTexts().getValue("roleplay.shift.cancel"));
            return;
        }

        if ((System.currentTimeMillis() / 1000) >= endsAt) {
            onShiftFinish(habbo, corpPosition);
            return;
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        int secondsLeft = (int) (endsAt - currentTimeInSeconds);
        int minutesLeft = secondsLeft / 60;

        habbo.shout(Emulator.getTexts().getValue("roleplay.shift.progress")
                .replace(":time", String.valueOf(minutesLeft > 0 ? minutesLeft : secondsLeft))
                .replace(":unit", minutesLeft > 0 ? "minutes" : "seconds"));
    }

    private void onShiftFinish(Habbo habbo, CorpPosition corpPosition) {
        habbo.shout(Emulator.getTexts().getValue("roleplay.shift.success")
                .replace(":credits", String.valueOf(corpPosition.getSalary())));
        habbo.getHabboRoleplayStats().setWorking(false);
        habbo.getHabboInfo().setCredits(habbo.getHabboInfo().getCredits() + corpPosition.getSalary());
        habbo.getHabboInfo().setMotto(this.oldMotto);
        habbo.getHabboInfo().changeClothes(this.oldFigure);
        habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
        habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo.getClient().getHabbo()).compose());
    }
}

