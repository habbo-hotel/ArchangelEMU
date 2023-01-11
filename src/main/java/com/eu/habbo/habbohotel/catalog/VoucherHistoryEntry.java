package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.database.DatabaseConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class VoucherHistoryEntry {
    @Getter
    private final int voucherId;
    @Getter
    private final int userId;
    @Getter
    private final int timestamp;

    public VoucherHistoryEntry(ResultSet set) throws SQLException {
        this.voucherId = set.getInt("voucher_id");
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.timestamp = set.getInt("timestamp");
    }

}
