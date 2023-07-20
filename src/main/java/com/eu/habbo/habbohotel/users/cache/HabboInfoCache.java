package com.eu.habbo.habbohotel.users.cache;

import com.eu.habbo.habbohotel.users.HabboInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Getter
public class HabboInfoCache {
    private final LinkedHashMap<Integer, HabboInfo> habboInfoCache;

    public HabboInfoCache() {
        this.habboInfoCache = new LinkedHashMap<Integer, HabboInfo>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, HabboInfo> eldest) {
                return false;
            }
        };
    }
}
