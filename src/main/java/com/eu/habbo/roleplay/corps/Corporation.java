package com.eu.habbo.roleplay.corps;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.CorporationPositionRepository;
import com.eu.habbo.roleplay.database.CorporationRepository;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Getter
public class Corporation  implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationPosition.class);
    @Getter
    private final Guild guild;
    @Getter
    private List<String> tags;
    @Getter
    private TIntObjectHashMap<CorporationPosition> positions;

    public CorporationPosition getPositionByOrderID(int orderID) {
        int[] keys = positions.keys();
        for (int key : keys) {
            CorporationPosition position = positions.get(key);
            if (position.getOrderID() == orderID) {
                return position;
            }
        }
        return null;
    }

    @Getter
    private TIntObjectHashMap<Habbo> invitedUsers;

    public void addInvitedUser(Habbo habbo) {
        this.invitedUsers.put(habbo.getHabboInfo().getId(), habbo);
    }

    public Habbo getInvitedUser(Habbo habbo) {
        return this.invitedUsers.get(habbo.getHabboInfo().getId());
    }

    public void removeInvitedUser(Habbo habbo) {
        this.invitedUsers.remove(habbo.getHabboInfo().getId());
    }


    public CorporationPosition getPositionByID(int positionID) {
        return this.positions.get(positionID);
    }

    public Corporation(ResultSet set) throws SQLException {
        int guildID = set.getInt("guild_id");
        this.guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildID);
        this.tags = Arrays.stream(set.getString("tags").split(";")).toList();
        this.positions = CorporationPositionRepository.getInstance().getAllCorporationPositions(this.guild.getId());
        this.invitedUsers = new TIntObjectHashMap<>();
    }

    @Override
    public void run() {
        CorporationRepository.getInstance().upsertCorporation(this);
        TIntObjectIterator<CorporationPosition> iterator = positions.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            CorporationPosition position = iterator.value();
            position.run();
        }
    }


}