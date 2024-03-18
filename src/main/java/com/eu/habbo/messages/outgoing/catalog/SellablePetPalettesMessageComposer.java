package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.pets.PetRace;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SellablePetPalettesMessageComposer extends MessageComposer {
    private final String petName;
    private final THashSet<PetRace> petRaces;


    @Override
    protected ServerMessage composeInternal() {
        if (this.petRaces == null)
            return null;
        this.response.init(Outgoing.sellablePetPalettesMessageComposer);
        this.response.appendString(this.petName);
        this.response.appendInt(this.petRaces.size());
        for (PetRace race : this.petRaces) {
            this.response.appendInt(race.getRace());
            this.response.appendInt(race.getColorOne());
            this.response.appendInt(race.getColorTwo());
            this.response.appendBoolean(race.isHasColorOne());
            this.response.appendBoolean(race.isHasColorTwo());
        }
        return this.response;
    }
}
