package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class NavigatorSearchResultBlocksComposer extends MessageComposer {
    private final String searchCode;
    private final String searchQuery;
    private final List<SearchResultList> resultList;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.navigatorSearchResultBlocksComposer);
        this.response.appendString(this.searchCode);
        this.response.appendString(this.searchQuery);

        this.response.appendInt(this.resultList.size()); //Count

        for (SearchResultList item : this.resultList) {
            item.serialize(this.response);
        }

        return this.response;
    }


}
