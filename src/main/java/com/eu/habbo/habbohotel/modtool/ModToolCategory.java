package com.eu.habbo.habbohotel.modtool;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

public class ModToolCategory {
    @Getter
    private final String name;
    @Getter
    private final TIntObjectMap<ModToolPreset> presets;

    public ModToolCategory(String name) {
        this.name = name;
        this.presets = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    }

    public void addPreset(ModToolPreset preset) {
        this.presets.put(preset.getId(), preset);
    }

}
