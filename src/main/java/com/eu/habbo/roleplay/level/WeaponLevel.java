package com.eu.habbo.roleplay.level;

public class WeaponLevel extends Level<String> {

    public WeaponLevel(int baseXP, int levelMultiplier, int linearIncrease) {
        super(baseXP, levelMultiplier, linearIncrease);
    }

    @Override
    public String getType() {
        return "Weapon";
    }
}