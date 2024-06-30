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

public class WorkShiftAction implements Runnable {
    private final Habbo habbo;
    private final long endsAt;

    private final Corp corp;
    private final CorpPosition corpPosition;

    private final String oldMotto;
    private final String oldFigure;

    public WorkShiftAction(Habbo habbo) {
        this.habbo = habbo;
        this.oldMotto = habbo.getHabboInfo().getMotto();
        this.oldFigure = habbo.getHabboInfo().getLook();
        this.corp = habbo.getHabboRoleplayStats().getCorp();
        this.corpPosition = habbo.getHabboRoleplayStats().getCorpPosition();
        this.endsAt = Instant.now().getEpochSecond() + Duration.ofMinutes(10).getSeconds();

        this.onShiftStart();
    }

    @Override
    public void run() {
        if (habbo.getRoomUnit().getRoom() == null) {
            return;
        }

        if ( (habbo.getRoomUnit().getRoom().getRoomInfo().getId() != this.corp.getGuild().getRoomId()) && !this.corpPosition.isCanWorkAnywhere()) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.shift.cancel"));
            return;
        }

        if ((System.currentTimeMillis() / 1000) >= this.endsAt) {
            this.onShiftFinish();
            return;
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        int secondsLeft = (int) (this.endsAt - currentTimeInSeconds);
        int minutesLeft = secondsLeft / 60;

        this.habbo.shout(Emulator.getTexts()
                .getValue("roleplay.shift.progress")
                .replace(":time", String.valueOf(minutesLeft > 0 ? minutesLeft : secondsLeft))
                .replace(":unit", minutesLeft > 0 ? "minutes" : "seconds"));

        Emulator.getThreading().run(this, 25000);
    }

    private void onShiftStart() {
        this.habbo.shout(Emulator.getTexts()
                .getValue("roleplay.shift.start")
                .replace(":position", this.corpPosition.getName())
        );
        this.habbo.getHabboRoleplayStats().setWorking(true);
        this.habbo.getHabboInfo().setMotto(this.corpPosition.getActivity());
        this.habbo.getHabboInfo().changeClothes(this.habbo.getHabboInfo().getGender() == HabboGender.M ? this.corpPosition.getMaleFigure() : this.corpPosition.getFemaleFigure());
        this.habbo.getClient().sendResponse(new FigureUpdateComposer(this.habbo));
        this.habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(this.habbo.getClient().getHabbo()).compose());
    }

    private void onShiftFinish() {
        this.habbo.shout(Emulator.getTexts()
                .getValue("roleplay.shift.success")
                .replace(":credits", String.valueOf(this.corpPosition.getSalary()))
        );
        this.habbo.getHabboRoleplayStats().setWorking(false);
        this.habbo.getHabboInfo().setCredits(this.habbo.getHabboInfo().getCredits() + this.corpPosition.getSalary());
        this.habbo.getHabboInfo().setMotto(this.oldMotto);
        this.habbo.getHabboInfo().changeClothes(this.oldFigure);
        this.habbo.getClient().sendResponse(new FigureUpdateComposer(this.habbo));
        this.habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(this.habbo.getClient().getHabbo()).compose());
    }

}
