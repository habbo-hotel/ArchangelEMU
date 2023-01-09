package com.eu.habbo.habbohotel.messenger;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
@Getter
@AllArgsConstructor
public class FriendRequest {
    private int id;
    private String username;
    private String look;

    public FriendRequest(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.username = set.getString("username");
        this.look = set.getString("look");
    }
}
