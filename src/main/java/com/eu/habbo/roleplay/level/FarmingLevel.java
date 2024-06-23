package com.eu.habbo.roleplay.level;

public class FarmingLevel extends Level<String> {

    public FarmingLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Farming";
    }
}