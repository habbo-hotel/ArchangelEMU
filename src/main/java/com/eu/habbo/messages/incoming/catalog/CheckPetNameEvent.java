package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.PetNameErrorComposer;
import org.apache.commons.lang3.StringUtils;

public class CheckPetNameEvent extends MessageHandler {
    public static int PET_NAME_LENGTH_MINIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.min");
    public static int PET_NAME_LENGTH_MAXIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.max");

    @Override
    public void handle() throws Exception {
        String petName = this.packet.readString();

        int minLength = Emulator.getConfig().getInt("hotel.pets.name.length.min");
        int maxLength = Emulator.getConfig().getInt("hotel.pets.name.length.max");

        if (petName.length() < PET_NAME_LENGTH_MINIMUM) {
            this.client.sendResponse(new PetNameErrorComposer(PetNameErrorComposer.NAME_TO_SHORT, minLength + ""));
        } else if (petName.length() > maxLength) {
            this.client.sendResponse(new PetNameErrorComposer(PetNameErrorComposer.NAME_TO_LONG, maxLength + ""));
        } else if (!StringUtils.isAlphanumeric(petName)) {
            this.client.sendResponse(new PetNameErrorComposer(PetNameErrorComposer.FORBIDDEN_CHAR, petName));
        } else {
            this.client.sendResponse(new PetNameErrorComposer(PetNameErrorComposer.NAME_OK, petName));
        }
    }
}
