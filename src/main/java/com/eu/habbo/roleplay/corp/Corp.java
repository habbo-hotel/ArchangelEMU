package com.eu.habbo.roleplay.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.CorpPositionRepository;
import com.eu.habbo.roleplay.database.CorpRepository;
import com.eu.habbo.roleplay.database.HabboRoleplayStatsRepository;
import com.eu.habbo.roleplay.users.HabboRoleplayStats;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class Corp implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpPosition.class);

    private final int guildID;

    @Getter
    private Set<CorpTag> tags;
    @Getter
    private TIntObjectHashMap<CorpPosition> positions;

    public void addPosition(CorpPosition corpPosition) {
        this.positions.put(corpPosition.getId(), corpPosition);
    }

    public void removePositionByID(int positionID) {
        this.positions.remove(positionID);
    }

    public CorpPosition getPositionByOrderID(int orderID) {
        int[] keys = positions.keys();
        for (int key : keys) {
            CorpPosition position = positions.get(key);
            if (position.getOrderID() == orderID) {
                return position;
            }
        }
        return null;
    }

    @Getter
    private TIntObjectHashMap<Habbo> invitedUsers;

    public Guild getGuild() {
        return Emulator.getGameEnvironment().getGuildManager().getGuild(this.guildID);
    }

    public void addInvitedUser(Habbo habbo) {
        this.invitedUsers.put(habbo.getHabboInfo().getId(), habbo);
    }

    public Habbo getInvitedUser(Habbo habbo) {
        return this.invitedUsers.get(habbo.getHabboInfo().getId());
    }

    public void removeInvitedUser(Habbo habbo) {
        this.invitedUsers.remove(habbo.getHabboInfo().getId());
    }

    public CorpPosition getPositionByID(int positionID) {
        return this.positions.get(positionID);
    }

    public Corp(ResultSet set) throws SQLException {
        this.guildID = set.getInt("guild_id");
        this.tags = new HashSet<>(Arrays.stream(set.getString("tags").split(","))
               .map(String::trim)
               .map(tagValue -> {
                   try {
                       return CorpTag.fromValue(tagValue);
                   } catch (IllegalArgumentException e) {
                       return null; // or handle default value
                   }
               })
               .toList());
        this.positions = CorpPositionRepository.getInstance().getAllCorporationPositions(this.getGuild().getId());
        this.invitedUsers = new TIntObjectHashMap<>();
    }

    @Override
    public void run() {
        CorpRepository.getInstance().upsertCorp(this);
        TIntObjectIterator<CorpPosition> iterator = positions.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            CorpPosition position = iterator.value();
            position.run();
        }
    }


}