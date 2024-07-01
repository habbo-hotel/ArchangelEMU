package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.database.HabboRoleplayStatsRepository;
import com.eu.habbo.roleplay.users.HabboRoleplayStats;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CorpEmployeeListComposer extends MessageComposer {
    private final int corpID;

    @Override
    protected ServerMessage composeInternal() {
        List<HabboRoleplayStats> habboRoleplayStatsList = HabboRoleplayStatsRepository.getInstance().getByCorpID(corpID);

        this.response.init(Outgoing.corpEmployeeListComposer);
        this.response.appendInt(this.corpID);
        this.response.appendInt(habboRoleplayStatsList.size());

        for (HabboRoleplayStats habboRoleplayStats : habboRoleplayStatsList) {
            this.response.appendString(
                    habboRoleplayStats.getUserID()
                            + ";" + habboRoleplayStats.getHabbo().getHabboInfo().getUsername()
                            + ";" + habboRoleplayStats.getHabbo().getHabboInfo().getLook()
                            + ";" + habboRoleplayStats.getCorpPosition().getId()
                            + ";" + habboRoleplayStats.getCorpPosition().getName());
        }

        return this.response;
    }
}
