package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.ApproveNameMessageComposer;
import org.apache.commons.lang3.StringUtils;

public class ApproveNameEvent extends MessageHandler {
    public static int PET_NAME_LENGTH_MINIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.min");
    public static int PET_NAME_LENGTH_MAXIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.max");

    @Override
    public void handle() {
        String petName = this.packet.readString();

         if (Emulator.getConfig().getBoolean("hotel.wordfilter.enabled", true) && !Emulator.getGameEnvironment().getWordFilter().filter(petName, this.client.getHabbo()).equals(petName)){
            this.client.sendResponse(new ApproveNameMessageComposer(ApproveNameMessageComposer.FORBIDDEN_WORDS, petName));
        } else if (petName.length() < PET_NAME_LENGTH_MINIMUM) {
            this.client.sendResponse(new ApproveNameMessageComposer(ApproveNameMessageComposer.NAME_TO_SHORT, PET_NAME_LENGTH_MINIMUM + ""));
        } else if (petName.length() > PET_NAME_LENGTH_MAXIMUM) {
            this.client.sendResponse(new ApproveNameMessageComposer(ApproveNameMessageComposer.NAME_TO_LONG, PET_NAME_LENGTH_MAXIMUM + ""));
        } else if (!StringUtils.isAlphanumeric(petName)) {
            this.client.sendResponse(new ApproveNameMessageComposer(ApproveNameMessageComposer.FORBIDDEN_CHAR, petName));
        }  else {
            this.client.sendResponse(new ApproveNameMessageComposer(ApproveNameMessageComposer.NAME_OK, petName));
        }
    }
}
