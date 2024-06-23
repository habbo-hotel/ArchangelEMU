package com.eu.habbo.roleplay.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class InteractionWeightBench extends InteractionEffectTile {
    public static String INTERACTION_TYPE = "rp_weight_bench";

    private Habbo habbo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private volatile boolean userOnItem = false;

    public InteractionWeightBench(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWeightBench(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        this.habbo = room.getHabbosOnItem(roomUnit.getCurrentItem()).iterator().next();

        if (this.habbo == null) {
            return;
        }

        this.habbo.shout(Emulator.getTexts().getValue("roleplay.weightbench.start"));

        userOnItem = true;

        int weightBenchTime = Emulator.getConfig().getInt("roleplay.weightbench.time", 5);

        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            if (userOnItem) {
                this.habbo.getHabboRoleplayStats().addStrengthXP(Emulator.getConfig().getInt("roleplay.weightbench.boost", 5));
            }
        }, weightBenchTime, weightBenchTime, TimeUnit.SECONDS);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        this.habbo.shout(Emulator.getTexts().getValue("roleplay.weightbench.stop"));

        this.habbo = null;

        userOnItem = false;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (scheduler != null) {
            this.shutdownScheduler();
        }
    }

    public void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}