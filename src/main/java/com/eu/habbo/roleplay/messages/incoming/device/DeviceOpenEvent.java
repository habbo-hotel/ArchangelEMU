package com.eu.habbo.roleplay.messages.incoming.device;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.interactions.InteractionPhone;
import com.eu.habbo.roleplay.interactions.InteractionPoliceLaptop;
import com.eu.habbo.roleplay.interactions.InteractionUsable;
import com.eu.habbo.roleplay.messages.outgoing.device.DeviceOpenComposer;

public class DeviceOpenEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();
        RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemID);

        if (item == null) {
            this.client.getHabbo().shout("invalid item");
            return;
        }

        if (!InteractionUsable.class.isAssignableFrom(item.getBaseItem().getInteractionType().getType())) {
            this.client.getHabbo().shout("device is not usable");
            return;
        }

        if (item.getBaseItem().getInteractionType().getType() == InteractionPhone.class) {
            this.client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.phone.take_out"));
            this.client.getHabbo().getRoomUnit().giveEffect(InteractionPhone.PHONE_EFFECT_ID, -1);
        }

        if (item.getBaseItem().getInteractionType().getType() == InteractionPoliceLaptop.class) {
            Corp corp = this.client.getHabbo().getHabboRoleplayStats().getCorp();

            if (corp == null) {
                this.client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.unemployed"));
                return;
            }

            if (!corp.getTags().contains(CorpTag.POLICE)) {
                this.client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.police_only"));
                return;
            }

            if (!this.client.getHabbo().getHabboRoleplayStats().isWorking()) {
                this.client.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
                return;
            }
            this.client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.police_laptop.take_out"));
            this.client.getHabbo().getRoomUnit().giveEffect(InteractionPoliceLaptop.LAPTOP_EFFECT_ID, -1);
        }

        this.client.sendResponse(new DeviceOpenComposer(item));
    }
}