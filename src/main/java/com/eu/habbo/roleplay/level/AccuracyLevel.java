package com.eu.habbo.roleplay.level;

public class AccuracyLevel extends Level<String> {

    public AccuracyLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Accuracy";
    }
}