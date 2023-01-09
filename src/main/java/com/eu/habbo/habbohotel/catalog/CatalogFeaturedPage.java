package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CatalogFeaturedPage implements ISerialize {
    private final int slotId;
    private final String caption;
    private final String image;
    private final Type type;
    private final int expireTimestamp;
    private final String pageName;
    private final int pageId;
    private final String productName;

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.slotId);
        message.appendString(this.caption);
        message.appendString(this.image);
        message.appendInt(this.type.type);
        switch (this.type) {
            case PAGE_NAME -> message.appendString(this.pageName);
            case PAGE_ID -> message.appendInt(this.pageId);
            case PRODUCT_NAME -> message.appendString(this.productName);
        }
        message.appendInt(Emulator.getIntUnixTimestamp() - this.expireTimestamp);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        PAGE_NAME(0),
        PAGE_ID(1),
        PRODUCT_NAME(2);

        private final int type;
    }
}