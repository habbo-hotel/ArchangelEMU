package com.eu.habbo.roleplay.skill;

public class FarmingSkill extends Skill<String> {

    public FarmingSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Farming";
    }
}