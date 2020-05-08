package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PixelScheduler extends Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PixelScheduler.class);

    public static boolean IGNORE_HOTEL_VIEW;
    public static boolean IGNORE_IDLED;

    public PixelScheduler() {
        super(Emulator.getConfig().getInt("hotel.auto.pixels.interval"));
        this.reloadConfig();
    }

    public void reloadConfig() {
        if (Emulator.getConfig().getBoolean("hotel.auto.pixels.enabled")) {
            IGNORE_HOTEL_VIEW = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.hotelview");
            IGNORE_IDLED = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.idled");
            if (this.disposed) {
                this.disposed = false;
                this.run();
            }
        } else {
            this.disposed = true;
        }
    }

    @Override
    public void run() {
        super.run();

        Habbo habbo;
        for (Map.Entry<Integer, Habbo> map : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
            habbo = map.getValue();
            try {
                if (habbo != null) {
                    if (habbo.getHabboInfo().getCurrentRoom() == null && IGNORE_HOTEL_VIEW)
                        continue;

                    if (habbo.getRoomUnit().isIdle() && IGNORE_IDLED)
                        continue;

                    habbo.givePixels(habbo.getHabboInfo().getRank().getPixelsTimerAmount());
                }
            } catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
    }

    public boolean isDisposed() {
        return this.disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }
}
