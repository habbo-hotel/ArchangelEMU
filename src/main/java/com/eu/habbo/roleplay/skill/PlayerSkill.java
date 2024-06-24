package com.eu.habbo.roleplay.skill;

public class PlayerSkill extends Skill<String> {

    public PlayerSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Player";
    }
}