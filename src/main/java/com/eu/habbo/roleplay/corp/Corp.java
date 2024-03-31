package com.eu.habbo.roleplay.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.CorpPositionRepository;
import com.eu.habbo.roleplay.database.CorpRepository;
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
public class Corp implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpPosition.class);
    @Getter
    private final Guild guild;
    @Getter
    private List<String> tags;
    @Getter
    private TIntObjectHashMap<CorpPosition> positions;

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
        this.guild = Emulator.getGameEnvironment().getGuildManager().getGuild(set.getInt("guild_id"));
        this.tags = Arrays.stream(set.getString("tags").split(";")).toList();
        this.positions = CorpPositionRepository.getInstance().getAllCorporationPositions(this.guild.getId());
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