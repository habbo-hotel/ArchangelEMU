package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpOpenComputerComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCorpComputer extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_corp_pc";

    public InteractionCorpComputer(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCorpComputer(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        Corp corp = client.getHabbo().getHabboRoleplayStats().getCorp();

        if (corp.getGuild().getOwnerId() != client.getHabbo().getHabboInfo().getId()) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.cor.not_the_owner"));
            return;
        }

        if (!client.getHabbo().getHabboRoleplayStats().isWorking()) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
            return;
        }

        client.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.computer.logged_in")
                .replace(":corpName", corp.getGuild().getName())
        );

        client.sendResponse(new CorpOpenComputerComposer(this.getId(), corp.getGuild().getId()));
    }
}