package com.eu.habbo.roleplay.level;

public abstract class Level<T> {
    private final int baseXP;
    private final int levelMultiplier;
    private final int linearIncrease;

    // Constructor to set the constants for the XP formula
    public Level(int baseXP, int levelMultiplier, int linearIncrease) {
        this.baseXP = baseXP;
        this.levelMultiplier = levelMultiplier;
        this.linearIncrease = linearIncrease;
    }

    // Abstract method to be implemented by subclasses to supply their specific type
    public abstract T getType();

    // Method to calculate XP required for a given level
    public int getXpForLevel(int level) {
        return baseXP + levelMultiplier * level * level + linearIncrease * level;
    }

    // Method to calculate the total XP required to reach a given level
    public int getTotalXpForLevel(int level) {
        int totalXp = 0;
        for (int i = 1; i <= level; i++) {
            totalXp += getXpForLevel(i);
        }
        return totalXp;
    }

    // Method to determine the current level based on XP
    public int getLevelForXp(int xp) {
        int level = 0;
        int totalXp = 0;

        while (totalXp <= xp) {
            level++;
            totalXp += getXpForLevel(level);
        }

        return level - 1; // Subtract 1 because loop exits after exceeding xp
    }

    // Method to determine the XP needed for the next level
    public int getXpToNextLevel(int currentXp) {
        int currentLevel = getLevelForXp(currentXp);
        int nextLevel = currentLevel + 1;
        int nextLevelTotalXp = getTotalXpForLevel(nextLevel);

        return nextLevelTotalXp - currentXp;
    }
}