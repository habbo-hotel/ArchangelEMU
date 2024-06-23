package com.eu.habbo.roleplay.level;

public class MeleeLevel extends Level<String> {

    public MeleeLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Melee";
    }
}