package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpPosition;

public class CorpEditPositionEvent extends MessageHandler {

    @Override
    public void handle() {
        int corpID = this.packet.readInt();
        int corpPositionID = this.packet.readInt();

        Corp corp = CorpManager.getInstance().getCorpByID(corpID);

        if (corp == null) {
            return;
        }

        if (corp.getGuild().getOwnerId() != this.client.getHabbo().getHabboInfo().getId()) {
            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.cor.not_the_owner"));
            return;
        }

        CorpPosition corpPosition = corp.getPositionByID(corpPositionID);

        if (corpPosition == null) {
            return;
        }

        corpPosition.setName(this.packet.readString());
        corpPosition.setActivity(this.packet.readString());
        corpPosition.setSalary(this.packet.readInt());
        corpPosition.setMaleFigure(this.packet.readString());
        corpPosition.setFemaleFigure(this.packet.readString());

        this.client.getHabbo().whisper(Emulator.getTexts()
                .getValue("roleplay.corp_position.edit_success")
                .replace(":position", corpPosition.getName())
        );

    }
}