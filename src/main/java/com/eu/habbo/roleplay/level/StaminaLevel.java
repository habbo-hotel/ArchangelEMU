package com.eu.habbo.roleplay.level;

public class StaminaLevel extends Level<String> {

    public StaminaLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Stamina";
    }
}