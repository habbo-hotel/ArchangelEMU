package com.eu.habbo.roleplay.skill;

public class WeaponSkill extends Skill<String> {

    public WeaponSkill(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Weapon";
    }
}