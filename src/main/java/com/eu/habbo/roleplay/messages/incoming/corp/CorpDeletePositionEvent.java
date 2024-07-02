package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpPosition;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.database.CorpPositionRepository;
import com.eu.habbo.roleplay.database.HabboRoleplayStatsRepository;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpPositionListComposer;
import com.eu.habbo.roleplay.users.HabboRoleplayStats;

import java.util.List;

public class CorpDeletePositionEvent extends MessageHandler {

    @Override
    public void handle() {
        int corpID = this.packet.readInt();
        int corpPositionID = this.packet.readInt();

        Corp corp = CorpManager.getInstance().getCorpByID(corpID);

        if (corp == null) {
            return;
        }

        CorpPosition corpPosition = corp.getPositionByID(corpPositionID);

        if (corpPosition == null) {
            return;
        }

        if (corpPosition.getId() == this.client.getHabbo().getHabboRoleplayStats().getCorpPosition().getId()) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.corp_position.cant_delete_your_job"));
            return;
        }

        if (corp.getGuild().getOwnerId() != this.client.getHabbo().getHabboInfo().getId()) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.cor.not_the_owner"));
            return;
        }

        List<Corp> welfareCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.WELFARE);

        if (welfareCorps.isEmpty()) {
            throw new RuntimeException("no welfare corp");
        }

        Corp welfareCorp = welfareCorps.get(0);
        CorpPosition welfarePosition = welfareCorp.getPositionByOrderID(1);

        if (welfarePosition == null) {
            throw new RuntimeException("no welfare position");
        }

        List<HabboRoleplayStats> habbosInPosition = HabboRoleplayStatsRepository.getInstance().getByCorpAndPositionID(corpID, corpPositionID);

        for (HabboRoleplayStats habboStats : habbosInPosition) {
            habboStats.setCorp(welfareCorp.getGuild().getId(), welfarePosition.getId());
        }

        CorpPositionRepository.getInstance().deleteCorpPositionByCorpAndOrder(corpPosition.getCorporationID(), corpPosition.getOrderID());

        corp.removePositionByID(corpPositionID);

        this.client.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.corp_position.delete_success")
                .replace(":position", corpPosition.getName())
        );

        this.client.sendResponse(new CorpPositionListComposer(corp));
    }
}