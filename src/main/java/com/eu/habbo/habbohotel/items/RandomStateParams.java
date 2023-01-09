package com.eu.habbo.habbohotel.items;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class RandomStateParams {
    @Getter
    private int states = -1;
    @Getter
    private int delay = -1;

    public RandomStateParams(String customparams) throws Exception {
        Arrays.stream(customparams.split(",")).forEach(pair -> {
            String[] keyValue = pair.split("=");

            if (keyValue.length != 2) return;

            switch (keyValue[0]) {
                case "states" -> this.states = Integer.parseInt(keyValue[1]);
                case "delay" -> this.delay = Integer.parseInt(keyValue[1]);
                default -> log.warn("RandomStateParams: unknown key: " + keyValue[0]);
            }
        });

        if (this.states < 0) throw new Exception("RandomStateParams: states not defined");
        if (this.delay < 0) throw new Exception("RandomStateParams: states not defined");
    }

}
