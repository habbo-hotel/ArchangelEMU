package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.*;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NavigatorSearchResultBlocksComposer;
import gnu.trove.map.hash.THashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class NewNavigatorSearchEvent extends MessageHandler {
    @Override
    public void handle() {
        String view = this.packet.readString();
        String query = this.packet.readString();

        if (view.equals("query")) view = "hotel_view";
        if (view.equals("groups")) view = "hotel_view";

        NavigatorFilter filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get(view);
        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption(view);

        if (filter == null) {
            List<Room> rooms = Emulator.getGameEnvironment().getNavigatorManager().getRoomsForCategory(view, this.client.getHabbo());

            if (rooms != null) {
                List<SearchResultList> resultLists = new ArrayList<>();
                resultLists.add(new SearchResultList(0, view, query, SearchAction.NONE, this.client.getHabbo().getHabboStats().getNavigatorWindowSettings().getListModeForCategory(view, ListMode.LIST), this.client.getHabbo().getHabboStats().getNavigatorWindowSettings().getDisplayModeForCategory(view, DisplayMode.VISIBLE), rooms, true, true, DisplayOrder.ACTIVITY, -1));
                this.client.sendResponse(new NavigatorSearchResultBlocksComposer(view, query, resultLists));
                return;
            }
        }

        String filterField = "anything";
        String part = query;
        NavigatorFilterField field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
        if (filter != null) {
            if (query.contains(":")) {
                String[] parts = query.split(":");

                if (parts.length > 1) {
                    filterField = parts[0];
                    part = parts[1];
                } else {
                    filterField = parts[0].replace(":", "");
                    if (!Emulator.getGameEnvironment().getNavigatorManager().filterSettings.containsKey(filterField)) {
                        filterField = "anything";
                    }
                }
            }

            if (Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField) != null) {
                field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
            }
        }

        if (field == null || query.isEmpty()) {
            if (filter == null)
                return;

            List<SearchResultList> resultLists = filter.getResult(this.client.getHabbo());
            Collections.sort(resultLists);

            if (!query.isEmpty()) {
                resultLists = toQueryResults(resultLists);
            }

            this.client.sendResponse(new NavigatorSearchResultBlocksComposer(view, query, resultLists));
            return;
        }

        if (filter == null) {
            filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get("hotel_view");
        }

        if (category == null) {
            category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption("hotel_view");
        }

        if (filter == null)
            return;

        try {
            List<SearchResultList> resultLists2 = filter.getResult(this.client.getHabbo(), field, part, category != null ? category.getId() : -1);
            List<SearchResultList> resultLists = new ArrayList<>();
            for(SearchResultList searchResultList : resultLists2) {
                List<Room> rooms = new ArrayList<>(searchResultList.getRooms());
                resultLists.add(new SearchResultList(searchResultList.getOrder(), searchResultList.getCode(), searchResultList.getQuery(), searchResultList.getAction(), searchResultList.getMode(), searchResultList.getHidden(), rooms, searchResultList.isFilter(), searchResultList.isShowInvisible(), searchResultList.getDisplayOrder(), searchResultList.getCategoryOrder()));
            }
            filter.filter(field.getField(), part, resultLists);
            resultLists = toQueryResults(resultLists);
            this.client.sendResponse(new NavigatorSearchResultBlocksComposer(view, query, resultLists));
        } catch (Exception e) {
            log.error("Caught exception", e);
        }

        /*
        try
        {

            List<SearchResultList> resultLists = new ArrayList<>(filter.getResult(this.client.getHabbo(), field, part, category != null ? category.getId() : -1));
            filter.filter(field.field, part, resultLists);

            Collections.sort(resultLists);
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
        }
        catch (Exception e)
        {
            log.error("Caught exception", e);
        }
        */
    }

    private ArrayList<SearchResultList> toQueryResults(List<SearchResultList> resultLists) {
        ArrayList<SearchResultList> nList = new ArrayList<>();
        THashMap<Integer, Room> searchRooms = new THashMap<>();

        for (SearchResultList li : resultLists) {
            for (Room room : li.getRooms()) {
                searchRooms.put(room.getId(), room);
            }
        }

        SearchResultList list = new SearchResultList(0, "query", "", SearchAction.NONE, ListMode.LIST, DisplayMode.VISIBLE, new ArrayList<>(searchRooms.values()), true, this.client.getHabbo().hasRight(Permission.ACC_ENTERANYROOM) || this.client.getHabbo().hasRight(Permission.ACC_ANYROOMOWNER), DisplayOrder.ACTIVITY, -1);
        nList.add(list);
        return nList;
    }
}
