package com.eu.habbo.habbohotel.users.cache;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Getter
public class HabboInfoCache implements Runnable {
    private static final long CACHE_DURATION = 10 * 60 * 1000;
    private final LinkedHashMap<Integer, Pair<Long, HabboInfo>> data;

    public HabboInfoCache() {
        this.data = new LinkedHashMap<Integer, Pair<Long, HabboInfo>>(16, 0.75f) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Pair<Long, HabboInfo>> eldest) {
                return cleanEntry(eldest);
            }
        };
    }

    public void add(HabboInfo habboInfo) {
        long currentMillis = Emulator.getIntUnixTimestamp();
        this.data.put(habboInfo.getId(), new Pair<>(currentMillis, habboInfo));
    }

    private boolean cleanEntry(Map.Entry<Integer, Pair<Long, HabboInfo>> entry) {
        long currentMillis = Emulator.getIntUnixTimestamp();
        long entryTimestamp = entry.getValue().getKey();
        return entryTimestamp < currentMillis - CACHE_DURATION;
    }

    public void verifyInfo(int userId) {
        this.data.remove(userId);
    }

    @Override
    public void run() {
        synchronized (this.data) {
            this.data.entrySet().removeIf(this::cleanEntry);
        }
    }
}
