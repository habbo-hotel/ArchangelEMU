package com.eu.habbo.roleplay.facility.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import com.eu.habbo.roleplay.corp.CorpPosition;
import com.eu.habbo.roleplay.corp.CorporationShift;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FacilityCorpManager {

    private static FacilityCorpManager instance;

    public static FacilityCorpManager getInstance() {
        if (instance == null) {
            instance = new FacilityCorpManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityCorpManager.class);

    private final Map<Integer, CorporationShift> activeUserShifts;

    private FacilityCorpManager() {
        long millis = System.currentTimeMillis();
        this.activeUserShifts = new HashMap<>();
        LOGGER.info("Corp Shift Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }


    public void startUserShift(Habbo habbo) {
        this.activeUserShifts.put(habbo.getHabboInfo().getId(), new CorporationShift(habbo));
        habbo.shout(Emulator.getTexts()
                .getValue("commands.roleplay.cmd_start_work_success")
                .replace(":corp", habbo.getHabboRoleplayStats().getCorp().getGuild().getName())
                .replace(":position", habbo.getHabboRoleplayStats().getCorpPosition().getName())
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

    public void cycle() {
        for (CorporationShift shift : activeUserShifts.values()) {
            this.cycleUserShift(shift.getHabbo());
        }
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

        long timeLeftInSeconds = (shift.getEndTime() - currentTime) / 1000 + 1;
        long minutesLeft = timeLeftInSeconds / 60;
        long secondsLeft = timeLeftInSeconds % 60;

        String timeLeftMessage = minutesLeft > 0
                ? minutesLeft + " minute" + (minutesLeft != 1 ? "s" : "")
                :  secondsLeft + " second" + (secondsLeft != 1 ? "s" : "");

        shift.getHabbo().shout(Emulator.getTexts()
                .getValue("commands.roleplay.corporation_shift_time_left")
                .replace(":timeLeft", timeLeftMessage)
        );
    }
}