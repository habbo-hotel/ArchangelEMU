package com.eu.habbo.roleplay.skill;

public class StrengthSkill extends Skill<String> {

    public StrengthSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Strength";
    }
}