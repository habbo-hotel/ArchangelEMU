package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.messages.outgoing.bank.BankOpenComputerComposer;
import com.eu.habbo.roleplay.messages.outgoing.corp.CashRegisterComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBankComputer extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_bank_pc";

    public InteractionBankComputer(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBankComputer(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        int corpID = Integer.parseInt(this.getExtraData());
        Corp corp = client.getHabbo().getHabboRoleplayStats().getCorp();

        if (corp == null) {
            if (!client.getHabbo().getHabboRoleplayStats().isWorking()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
                return;
            }

            if (client.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() != corp.getGuild().getRoomId()) {
                client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_at_work"));
                return;
            }
        }

        if (!corp.getTags().contains(CorpTag.BANK)) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.corp_not_a_bank"));
            return;
        }

        client.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.computer.logged_in")
                .replace(":corpName", corp.getGuild().getName())
        );

        client.sendResponse(new BankOpenComputerComposer(this.getId(), corpID));
    }
}