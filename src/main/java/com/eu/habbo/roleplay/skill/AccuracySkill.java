package com.eu.habbo.roleplay.skill;

public class AccuracySkill extends Skill<String> {

    public AccuracySkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Accuracy";
    }
}