package com.eu.habbo.roleplay.level;

public class StrengthLevel extends Level<String> {

    public StrengthLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Strength";
    }
}