package com.eu.habbo.habbohotel.modtool;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

public class CfhCategory {
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final TIntObjectMap<CfhTopic> topics;

    public CfhCategory(int id, String name) {
        this.id = id;
        this.name = name;
        this.topics = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    }

    public void addTopic(CfhTopic topic) {
        this.topics.put(topic.getId(), topic);
    }

}