package com.eu.habbo.roleplay.users;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class HabboBankAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboTonic.class);

    @Getter
    private final int id;

    @Getter
    private final int corpID;

    @Getter
    private final int userID;

    @Getter
    @Setter
    private int checkingBalance;

    @Getter
    private final int createdAt;

    @Getter
    private final int updatedAt;

    public HabboBankAccount(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.corpID = set.getInt("corp_id");
        this.userID = set.getInt("user_id");
        this.checkingBalance = set.getInt("checking_balance");
        this.createdAt = set.getInt("created_at");
        this.updatedAt = set.getInt("updated_at");
    }
}