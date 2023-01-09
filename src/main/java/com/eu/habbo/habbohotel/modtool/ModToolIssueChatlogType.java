package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolIssueChatlogType {
    NORMAL(0),
    CHAT(1),
    IM(2),
    FORUM_THREAD(3),
    FORUM_COMMENT(4),
    SELFIE(5),
    PHOTO(6);

    private final int type;

}
