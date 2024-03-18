package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.TargetOffer;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.catalog.TargetedOfferComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MOTDNotificationComposer;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

public class PromoteTargetOfferCommand extends Command {
    public PromoteTargetOfferCommand() {
        super("cmd_promote_offer");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length <= 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_promote_offer.not_found"));
            return true;
        }

        String offerKey = params[1];

        if (offerKey.equalsIgnoreCase(getTextsValue("commands.cmd_promote_offer.info"))) {
            THashMap<Integer, TargetOffer> targetOffers = Emulator.getGameEnvironment().getCatalogManager().targetOffers;
            String[] textConfig = getTextsValue("commands.cmd_promote_offer.list").replace("%amount%", targetOffers.size() + "").split("<br>");

            String entryConfig = getTextsValue("commands.cmd_promote_offer.list.entry");
            List<String> message = new ArrayList<>();

            for (String pair : textConfig) {
                if (pair.contains("%list%")) {
                    for (TargetOffer offer : targetOffers.values()) {
                        message.add(entryConfig.replace("%id%", offer.getId() + "").replace("%title%", offer.getTitle()).replace("%description%", offer.getDescription().substring(0, 25)));
                    }
                } else {
                    message.add(pair);
                }
            }

            gameClient.sendResponse(new MOTDNotificationComposer(message));
        } else {
            int offerId = 0;
            try {
                offerId = Integer.parseInt(offerKey);
            } catch (Exception ignored) {
            }

            if (offerId > 0) {
                TargetOffer offer = Emulator.getGameEnvironment().getCatalogManager().getTargetOffer(offerId);

                if (offer != null) {
                    TargetOffer.ACTIVE_TARGET_OFFER_ID = offer.getId();
                    gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_promote_offer").replace("%id%", offerKey).replace("%title%", offer.getTitle()));

                    Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()
                            .forEach(habbo -> habbo.getClient().sendResponse(new TargetedOfferComposer(habbo, offer)));
                }
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_promote_offer.not_found"));
                return true;
            }
        }

        return true;
    }
}