package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpPosition;
import com.eu.habbo.roleplay.database.CorpPositionRepository;

public class CorpCreatePositionEvent extends MessageHandler {

    @Override
    public void handle() {
        int corpID = this.packet.readInt();
        int orderID = this.packet.readInt();
        String name = this.packet.readString();
        String description = this.packet.readString();
        int salary = this.packet.readInt();
        String maleFigure = this.packet.readString();
        String femaleFigure = this.packet.readString();
        boolean canHire = this.packet.readBoolean();
        boolean canFire = this.packet.readBoolean();
        boolean canPromote = this.packet.readBoolean();
        boolean canDemote = this.packet.readBoolean();
        boolean canWorkAnywhere = this.packet.readBoolean();

        Corp corp = CorpManager.getInstance().getCorpByID(corpID);

        if (corp == null) {
            return;
        }

        if (corp.getGuild().getOwnerId() != this.client.getHabbo().getHabboInfo().getId()) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.cor.not_the_owner"));
            return;
        }

        CorpPositionRepository.getInstance().upsertCorpPosition(
                    corpID,
                    orderID,
                    name,
                    description,
                    salary,
                    maleFigure,
                    femaleFigure,
                    canHire,
                    canFire,
                    canPromote,
                    canDemote,
                    canWorkAnywhere
        );

        CorpPosition corpPosition = CorpPositionRepository.getInstance().getCorpPosition(corp.getGuildID(), orderID);

        corp.addPosition(corpPosition);

        this.client.getHabbo().whisper(Emulator.getTexts()
                .getValue("roleplay.corp_position.create_success")
                .replace(":position", name)
        );

    }
}
