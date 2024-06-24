package com.eu.habbo.roleplay.skill;

public class StaminaSkill extends Skill<String> {

    public StaminaSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Stamina";
    }
}