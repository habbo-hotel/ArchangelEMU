package com.eu.habbo.roleplay.skill;

public class FishingSkill extends Skill<String> {

    public FishingSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Fishing";
    }
}