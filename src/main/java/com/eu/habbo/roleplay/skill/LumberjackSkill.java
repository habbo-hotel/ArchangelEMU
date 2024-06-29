package com.eu.habbo.roleplay.skill;

public class LumberjackSkill extends Skill<String> {

    public LumberjackSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Lumberjack";
    }
}