package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PetTrainingPanelComposer extends MessageComposer {
    private final Pet pet;


    @Override
    protected ServerMessage composeInternal() {
        List<PetCommand> enabled = new ArrayList<>();
        Collections.sort(this.pet.getPetData().getPetCommands());

        this.response.init(Outgoing.petTrainingPanelComposer);
        this.response.appendInt(this.pet.getId());
        this.response.appendInt(this.pet.getPetData().getPetCommands().size());

        for (PetCommand petCommand : this.pet.getPetData().getPetCommands()) {
            this.response.appendInt(petCommand.getId());

            if (this.pet.getLevel() >= petCommand.getLevel()) {
                enabled.add(petCommand);
            }
        }

        if (!enabled.isEmpty()) {
            Collections.sort(enabled);
        }

        this.response.appendInt(enabled.size());

        for (PetCommand petCommand : enabled) {
            this.response.appendInt(petCommand.getId());
        }

        return this.response;
    }
}
