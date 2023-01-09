package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.messages.ServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolChatRecordDataContext {
    ROOM_NAME("roomName", 2),
    ROOM_ID("roomId", 1),
    GROUP_ID("groupId", 1),
    THREAD_ID("threadId", 1),
    MESSAGE_ID("messageId", 1),
    PHOTO_ID("extraDataId", 2);

    private  final String key;
    private  final int type;


    public void append(final ServerMessage message) {
        message.appendString(this.key);
        message.appendByte(this.type);
    }
}