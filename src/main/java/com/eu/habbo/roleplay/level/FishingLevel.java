package com.eu.habbo.roleplay.level;

public class FishingLevel extends Level<String> {

    public FishingLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Fishing";
    }
}