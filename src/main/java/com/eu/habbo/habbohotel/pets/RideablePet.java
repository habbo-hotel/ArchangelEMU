package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RideablePet extends Pet {

    @Setter
    @Getter
    private Habbo rider;
    private boolean hasSaddle;
    @Setter
    private boolean anyoneCanRide;
    @Setter
    @Getter
    private int saddleItemId;

    public RideablePet(ResultSet set) throws SQLException {
        super(set);
        this.rider = null;
    }

    public RideablePet(int type, int race, String color, String name, int userId) {
        super(type, race, color, name, userId);
        this.rider = null;
    }

    public boolean hasSaddle() {
        return this.hasSaddle;
    }

    public void hasSaddle(boolean hasSaddle) {
        this.hasSaddle = hasSaddle;
    }

    public boolean anyoneCanRide() {
        return this.anyoneCanRide;
    }

}
