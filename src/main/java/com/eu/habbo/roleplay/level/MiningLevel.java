package com.eu.habbo.roleplay.level;

public class MiningLevel extends Level<String> {

    public MiningLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Mining";
    }
}