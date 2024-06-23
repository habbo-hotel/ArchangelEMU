package com.eu.habbo.roleplay.level;

public class PlayerLevel extends Level<String> {

    public PlayerLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Player";
    }
}