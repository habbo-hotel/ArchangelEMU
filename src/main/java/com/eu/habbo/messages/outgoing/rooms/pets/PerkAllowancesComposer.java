package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PerkAllowancesComposer extends MessageComposer {
    public static final int CLOSE_WIDGET = 0;
    public static final int NAME_TOO_SHORT = 1;
    public static final int NAME_TOO_LONG = 2;
    public static final int CONTAINS_INVALID_CHARS = 3;
    public static final int FORBIDDEN_WORDS = 4;

    private final int itemId;
    private final int errorCode;
    private final String errorString;



    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.openPetPackageResultMessageComposer);
        this.response.appendInt(this.itemId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.errorString);
        return this.response;
    }
}