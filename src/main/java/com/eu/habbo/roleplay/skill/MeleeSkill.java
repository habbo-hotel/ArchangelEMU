package com.eu.habbo.roleplay.skill;

public class MeleeSkill extends Skill<String> {

    public MeleeSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Melee";
    }
}