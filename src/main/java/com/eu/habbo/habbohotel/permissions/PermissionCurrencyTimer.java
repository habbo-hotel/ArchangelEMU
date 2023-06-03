package com.eu.habbo.habbohotel.permissions;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionCurrencyTimer {
    @Getter
    private final int id;

    @Getter
    private final int groupId;

    @Getter
    private final int currencyType;

    @Getter
    private final int amount;

    public PermissionCurrencyTimer(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.groupId = set.getInt("group_id");
        this.currencyType = set.getInt("currency_type");
        this.amount = set.getInt("amount");
    }
}
