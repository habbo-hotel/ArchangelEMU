package com.eu.habbo.habbohotel.users.clothingvalidation;

import lombok.Getter;

import java.util.TreeMap;

public class FiguredataPalette {
    @Getter
    private final int id;
    private final TreeMap<Integer, FiguredataPaletteColor> colors;

    public FiguredataPalette(int id) {
        this.id = id;
        this.colors = new TreeMap<>();
    }

    public void addColor(FiguredataPaletteColor color) {
        this.colors.put(color.getId(), color);
    }

    public FiguredataPaletteColor getColor(int colorId) {
        return this.colors.get(colorId);
    }

    public FiguredataPaletteColor getFirstNonHCColor() {
        for(FiguredataPaletteColor color : this.colors.values()) {
            if(!color.isClub() && color.isSelectable())
                return color;
        }

        return this.colors.size() > 0 ? this.colors.entrySet().iterator().next().getValue() : null;
    }

}
