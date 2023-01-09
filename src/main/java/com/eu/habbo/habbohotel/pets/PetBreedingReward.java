package com.eu.habbo.habbohotel.pets;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
@Getter
public class PetBreedingReward {
    private  final int petType;

    private  final int rarityLevel;

    private  final int breed;

    public PetBreedingReward(ResultSet set) throws SQLException {
        this.petType = set.getInt("pet_type");
        this.rarityLevel = set.getInt("rarity_level");
        this.breed = set.getInt("breed");
    }
}