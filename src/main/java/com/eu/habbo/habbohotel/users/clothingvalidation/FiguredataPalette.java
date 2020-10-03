package com.eu.habbo.habbohotel.users.clothingvalidation;

import java.util.Map;
import java.util.TreeMap;

public class FiguredataPalette {
    public int id;
    public Map<Integer, FiguredataPaletteColor> colors;

    public FiguredataPalette(int id) {
        this.id = id;
        this.colors = new TreeMap<>();
    }

    public void addColor(FiguredataPaletteColor color) {
        this.colors.put(color.id, color);
    }

    public FiguredataPaletteColor getColor(int colorId) {
        return this.colors.get(colorId);
    }

    /**
     * @return First non-club and selectable color
     */
    public FiguredataPaletteColor getFirstNonHCColor() {
        for(FiguredataPaletteColor color : this.colors.values()) {
            if(!color.club && color.selectable)
                return color;
        }

        return this.colors.size() > 0 ? this.colors.entrySet().iterator().next().getValue() : null;
    }

}
