package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.guides.GuardianVote;
import com.eu.habbo.habbohotel.guides.GuardianVoteType;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuardianNotAccepted implements Runnable {
    private final GuardianTicket ticket;
    private final Habbo habbo;

    @Override
    public void run() {
        GuardianVote vote = this.ticket.getVoteForGuardian(this.habbo);

        if (vote != null) {
            if (vote.type == GuardianVoteType.SEARCHING) {
                Emulator.getGameEnvironment().getGuideManager().acceptTicket(this.habbo, false);
            }
        }
    }
}
