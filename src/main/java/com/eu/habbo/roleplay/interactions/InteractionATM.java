package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.messages.outgoing.bank.BankOpenATMComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionATM extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_bank_atm";

    public InteractionATM(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionATM(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        int corpID = Integer.parseInt(this.getExtraData());
        Corp bankCorp = CorpManager.getInstance().getCorpByID(corpID);

        if (bankCorp == null) {
            if (!client.getHabbo().getHabboRoleplayStats().isWorking()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
                return;
            }

            if (this.getOwnerInfo().getId() != client.getHabbo().getHabboInfo().getId()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.device.not_setup"));
                return;
            }

            client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.device.set_up"));
            client.sendResponse(new BankOpenATMComposer(this.getId(), corpID));
            return;
        }

        if (!bankCorp.getTags().contains(CorpTag.BANK)) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.corp_not_a_bank"));
            return;
        }

        client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.bank.atm_insert_card"));
        client.sendResponse(new BankOpenATMComposer(this.getId(), corpID));
    }
}