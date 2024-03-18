package com.eu.habbo.habbohotel.users.clothingvalidation;

import lombok.Getter;

import java.util.TreeMap;

@Getter

public class FiguredataSettype {
    private final String type;
    private final int paletteId;
    private final boolean mandatoryMale0;
    private final boolean mandatoryFemale0;
    private final boolean mandatoryMale1;
    private final boolean mandatoryFemale1;
    private final TreeMap<Integer, FiguredataSettypeSet> sets;

    public FiguredataSettype(String type, int paletteId, boolean mandatoryMale0, boolean mandatoryFemale0, boolean mandatoryMale1, boolean mandatoryFemale1) {
        this.type = type;
        this.paletteId = paletteId;
        this.mandatoryMale0 = mandatoryMale0;
        this.mandatoryFemale0 = mandatoryFemale0;
        this.mandatoryMale1 = mandatoryMale1;
        this.mandatoryFemale1 = mandatoryFemale1;
        this.sets = new TreeMap<>();
    }

    public void addSet(FiguredataSettypeSet set) {
        this.sets.put(set.getId(), set);
    }

    public FiguredataSettypeSet getSet(int id) {
        return this.sets.get(id);
    }

    /**
     * @param gender Gender (M/F)
     * @return First non-sellable and selectable set for given gender
     */
    public FiguredataSettypeSet getFirstSetForGender(String gender) {
        for(FiguredataSettypeSet set : this.sets.descendingMap().values()) {
            if((set.getGender().equalsIgnoreCase(gender) || set.getGender().equalsIgnoreCase("u")) && !set.isSellable() && set.isSelectable()) {
                return set;
            }
        }

        return this.sets.size() > 0 ? this.sets.descendingMap().entrySet().iterator().next().getValue() : null;
    }

    /**
     * @param gender Gender (M/F)
     * @return First non-club, non-sellable and selectable set for given gender
     */
    public FiguredataSettypeSet getFirstNonHCSetForGender(String gender) {
        for(FiguredataSettypeSet set : this.sets.descendingMap().values()) {
            if((set.getGender().equalsIgnoreCase(gender) || set.getGender().equalsIgnoreCase("u")) && !set.isClub() && !set.isSellable() && set.isSelectable()) {
                return set;
            }
        }

        return getFirstSetForGender(gender);
    }
}
