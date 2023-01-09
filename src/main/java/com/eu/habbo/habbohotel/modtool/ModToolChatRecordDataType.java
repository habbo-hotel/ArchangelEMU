package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolChatRecordDataType {
    UNKNOWN(0),
    ROOM_TOOL(1),
    IM_SESSION(2),
    FORUM_THREAD(3),
    FORUM_MESSAGE(4),
    SELFIE(5),
    PHOTO_REPORT(6);

    private  final int type;

}