package com.eu.habbo.habbohotel.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ListMode {
    LIST(0),
    THUMBNAILS(1),
    FORCED_THUNBNAILS(2);

    private final int type;


    public static ListMode fromType(int type) {
        for (ListMode m : ListMode.values()) {
            if (m.type == type) {
                return m;
            }
        }

        return LIST;
    }
}