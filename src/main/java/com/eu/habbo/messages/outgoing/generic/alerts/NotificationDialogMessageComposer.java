package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NotificationDialogMessageComposer extends MessageComposer {
    private final String errorKey;
    private final THashMap<String, String> keys;

    public NotificationDialogMessageComposer(String errorKey, String message) {
        this.errorKey = errorKey;
        this.keys = new THashMap<>();
        this.keys.put("message", message);
    }

    public NotificationDialogMessageComposer(String errorKey) {
        this.errorKey = errorKey;
        this.keys = new THashMap<>();
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.notificationDialogMessageComposer);
        this.response.appendString(this.errorKey);
        this.response.appendInt(this.keys.size());
        for (Map.Entry<String, String> set : this.keys.entrySet()) {
            this.response.appendString(set.getKey());
            this.response.appendString(set.getValue());
        }
        return this.response;
    }
}
