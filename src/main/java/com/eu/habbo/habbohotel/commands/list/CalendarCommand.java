package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.campaign.CalendarCampaign;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.events.calendar.CampaignCalendarDataMessageComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.InClientLinkMessageComposer;

import java.sql.Timestamp;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class CalendarCommand extends Command {
    public CalendarCommand() {
        super("cmd_calendar");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (Emulator.getConfig().getBoolean("hotel.calendar.enabled")) {
            String campaignName = Emulator.getConfig().getValue("hotel.calendar.default");

            if (params.length > 1 && gameClient.getHabbo().hasCommand("cmd_calendar_staff")) {
                campaignName = params[1];
            }

            CalendarCampaign campaign = Emulator.getGameEnvironment().getCalendarManager().getCalendarCampaign(campaignName);

            if (campaign == null) return false;

            int daysBetween = (int) DAYS.between(new Timestamp(campaign.getStartTimestamp() * 1000L).toInstant(), new Date().toInstant());

            if (daysBetween >= 0) {
                gameClient.sendResponse(new CampaignCalendarDataMessageComposer(campaign.getName(), campaign.getImage(), campaign.getTotalDays(), daysBetween, gameClient.getHabbo().getHabboStats().getCalendarRewardsClaimed(), campaign.getLockExpired()));
                gameClient.sendResponse(new InClientLinkMessageComposer("openView/calendar"));
            }
        }

        return true;
    }
}
