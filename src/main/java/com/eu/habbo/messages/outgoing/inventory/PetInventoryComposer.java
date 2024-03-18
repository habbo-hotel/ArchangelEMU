package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.iterator.TIntObjectIterator;
import lombok.AllArgsConstructor;

import java.util.NoSuchElementException;

@AllArgsConstructor
public class PetInventoryComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petInventoryComposer);

        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendInt(this.habbo.getInventory().getPetsComponent().getPetsCount());

        TIntObjectIterator<Pet> petIterator = this.habbo.getInventory().getPetsComponent().getPets().iterator();

        for (int i = this.habbo.getInventory().getPetsComponent().getPets().size(); i-- > 0; ) {
            try {
                petIterator.advance();
            } catch (NoSuchElementException e) {
                break;
            }
            petIterator.value().serialize(this.response);
        }

        return this.response;
    }
}
