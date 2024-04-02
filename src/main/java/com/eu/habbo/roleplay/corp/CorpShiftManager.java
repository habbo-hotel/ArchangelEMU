package com.eu.habbo.roleplay.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CorpShiftManager {

    private static CorpShiftManager instance;

    public static CorpShiftManager getInstance() {
        if (instance == null) {
            instance = new CorpShiftManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpShiftManager.class);

    private final Map<Integer, CorporationShift> activeUserShifts;
    private final ScheduledExecutorService scheduler;

    private CorpShiftManager() {
        long millis = System.currentTimeMillis();
        this.activeUserShifts = new HashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.startShiftManager();
        LOGGER.info("Corp Shift Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }


    public void startUserShift(Habbo habbo) {
        this.activeUserShifts.put(habbo.getHabboInfo().getId(), new CorporationShift(habbo));
        habbo.shout(Emulator.getTexts()
                .getValue("commands.roleplay.cmd_start_work_success")
                .replace("%corp%", habbo.getHabboRoleplayStats().getCorp().getGuild().getName())
                .replace("%position%", habbo.getHabboRoleplayStats().getCorpPosition().getName())
        );

        String jobUniform = habbo.getHabboInfo().getGender() == HabboGender.M ? habbo.getHabboRoleplayStats().getCorpPosition().getMaleFigure() : habbo.getHabboRoleplayStats().getCorpPosition().getFemaleFigure();
        habbo.getHabboInfo().changeClothes(jobUniform);
        habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
        habbo.getHabboInfo().setMotto(habbo.getHabboRoleplayStats().getCorpPosition().getActivity());
        habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo).compose());
        habbo.getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(habbo).compose());
        this.cycleUserShift(habbo);
    }

    public void stopUserShift(Habbo habbo) {
        this.stopUserShift(habbo, false, false);
    }

    public void stopUserShift(Habbo habbo, boolean shiftCompleted, boolean startNewShift) {
        CorporationShift userShift = this.activeUserShifts.get(habbo.getHabboInfo().getId());
        if (!this.isUserWorking(habbo) || userShift == null) {
            habbo.whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_stop_work_no_shift"));
            return;
        }
        CorpPosition userPosition =  habbo.getHabboRoleplayStats().getCorpPosition();

        if (shiftCompleted) {
            habbo.whisper(Emulator.getTexts().getValue("commands.roleplay.corporation_shift_paid").replace(":salary", String.valueOf(userPosition.getSalary())));
            habbo.giveCredits(userPosition.getSalary());
        }

        if (!shiftCompleted) {
            habbo.whisper(Emulator.getTexts().getValue("commands.roleplay.corporation_shift_not_complete"));
        }

        this.activeUserShifts.remove(habbo.getHabboInfo().getId());

        if (startNewShift) {
            this.startUserShift(habbo);
        }

        if (!startNewShift) {
            habbo.shout(Emulator.getTexts().getValue("commands.roleplay.corporation_shift_stopped"));
            habbo.getHabboInfo().changeClothes(userShift.getOldLook());
            habbo.getHabboInfo().setMotto(Emulator.getTexts().getValue("commands.roleplay.cmd_stop_work_motto"));
            habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
            if (habbo.getRoomUnit().getRoom() != null) {
                habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo).compose());
                habbo.getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(habbo).compose());
            }
        }
    }

    public boolean isUserWorking(Habbo habbo) {
        return this.getUserShift(habbo) != null;
    }

    public CorporationShift getUserShift(Habbo habbo) {
        return this.activeUserShifts.get(habbo.getHabboInfo().getId());
    }

    public void startShiftManager() {
        Runnable userShift = () -> {
            for (CorporationShift shift : activeUserShifts.values()) {
               this.cycleUserShift(shift.getHabbo());
            }
        };

        // Schedule the task to run every 5 seconds
        scheduler.scheduleAtFixedRate(userShift, 0, 60, TimeUnit.SECONDS);
    }

    private void cycleUserShift(Habbo habbo) {
        CorporationShift shift = this.activeUserShifts.get(habbo.getHabboInfo().getId());
        if (shift == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime >= shift.getEndTime()) {
            this.stopUserShift(shift.getHabbo(), true, true);
            return;
        }

        long ONE_MINUTE_IN_MS = 60000;

        long timeLeft = (shift.getEndTime() - currentTime) / ONE_MINUTE_IN_MS + 1;

        shift.getHabbo().shout(Emulator.getTexts()
                .getValue("commands.roleplay.corporation_shift_time_left")
                .replace(":minutes", Long.toString(timeLeft))
        );
    }

    public void stopShiftManager() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public void dispose() {
        this.stopShiftManager();
    }
}