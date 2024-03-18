package com.eu.habbo.habbohotel.hotelview;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelViewManager {

    @Getter
    private final HallOfFame hallOfFame;
    @Getter
    private final NewsList newsList;

    public HotelViewManager() {
        long millis = System.currentTimeMillis();
        this.hallOfFame = new HallOfFame();
        this.newsList = new NewsList();

        log.info("Hotelview Manager -> Loaded! ({} MS)", System.currentTimeMillis() - millis);
    }

    public void dispose() {
        log.info("HotelView Manager -> Disposed!");
    }

}
