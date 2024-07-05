package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class CorpListComposer extends MessageComposer {

    @Override
    protected ServerMessage composeInternal() {
        List<Corp> corps = CorpManager.getInstance().getCorps();

        this.response.init(Outgoing.corpListComposer);
        this.response.appendInt(corps.size());

        // Append the sorted positions to the response
        for (Corp corp : corps) {
            this.response.appendString(
                    corp.getGuild().getId()
                            + ";" + corp.getGuild().getName()
            );
        }

        return this.response;
    }
}
