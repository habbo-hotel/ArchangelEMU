package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.actions.MiningAction;
import com.eu.habbo.roleplay.database.HabboLicenseRepository;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.users.HabboLicense;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMiningOre extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_mining_ore";

    public InteractionMiningOre(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionMiningOre(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        HabboLicense miningLicense = HabboLicenseRepository.getInstance().getByUserAndLicense(client.getHabbo().getHabboInfo().getId(), LicenseType.MINING);

        if (miningLicense == null) {
            client.getHabbo().whisper("you dont have a valid mining license");
            return;
        }
        Emulator.getThreading().run(new MiningAction(client.getHabbo(), client.getHabbo().getHabboInfo().getMotto(), this, client.getHabbo().getRoomUnit().getLastRoomTile()));
    }

}