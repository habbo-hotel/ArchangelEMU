package com.eu.habbo.habbohotel.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MarketPlaceOffer implements Runnable {
    public int average;
    public int count;
    @Getter
    @Setter
    private int offerId;
    private final Item baseItem;

    private final int itemId;

    @Getter
    private int price;
    @Getter
    private int limitedStack;
    @Getter
    private int limitedNumber;
    @Getter
    private int timestamp = Emulator.getIntUnixTimestamp();
    @Getter
    @Setter
    private int soldTimestamp = 0;
    @Getter
    @Setter
    private MarketPlaceState state = MarketPlaceState.OPEN;
    @Setter
    private boolean needsUpdate = false;

    public MarketPlaceOffer(ResultSet set, boolean privateOffer) throws SQLException {
        this.offerId = set.getInt("id");
        this.price = set.getInt("price");
        this.timestamp = set.getInt("timestamp");
        this.soldTimestamp = set.getInt("sold_timestamp");
        this.baseItem = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("base_item_id"));
        this.state = MarketPlaceState.getType(set.getInt("state"));
        this.itemId = set.getInt("item_id");

        if (!set.getString("ltd_data").split(":")[1].equals("0")) {
            this.limitedStack = Integer.parseInt(set.getString("ltd_data").split(":")[0]);
            this.limitedNumber = Integer.parseInt(set.getString("ltd_data").split(":")[1]);
        }

        if (!privateOffer) {
            this.average = set.getInt("avg");
            this.count = set.getInt("number");
            this.price = set.getInt("minPrice");
        }
    }

    public MarketPlaceOffer(HabboItem item, int price, Habbo habbo) {
        this.price = price;
        this.baseItem = item.getBaseItem();
        this.itemId = item.getId();
        if (item.getLimitedSells() > 0) {
            this.limitedNumber = item.getLimitedSells();
            this.limitedStack = item.getLimitedStack();
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO marketplace_items (item_id, user_id, price, timestamp, state) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, item.getId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, this.price);
            statement.setInt(4, this.timestamp);
            statement.setString(5, this.state.getState() + "");
            statement.execute();

            try (ResultSet id = statement.getGeneratedKeys()) {
                while (id.next()) {
                    this.offerId = id.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public static void insert(MarketPlaceOffer offer, Habbo habbo) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO marketplace_items VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, offer.getItemId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, offer.getPrice());
            statement.setInt(4, offer.getTimestamp());
            statement.setInt(5, offer.getSoldTimestamp());
            statement.setString(6, offer.getState().getState() + "");
            statement.execute();

            try (ResultSet id = statement.getGeneratedKeys()) {
                while (id.next()) {
                    offer.setOfferId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }


    @Override
    public void run() {
        if (this.needsUpdate) {
            this.needsUpdate = false;
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE marketplace_items SET state = ?, sold_timestamp = ? WHERE id = ?")) {
                statement.setInt(1, this.state.getState());
                statement.setInt(2, this.soldTimestamp);
                statement.setInt(3, this.offerId);
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }

    public int getType() {
        if (this.limitedStack > 0) {
            return 3;
        }

        return this.baseItem.getType().equals(FurnitureType.WALL) ? 2 : 1;
    }

    public int getItemId() {
        return this.baseItem.getSpriteId();
    }


    public int getSoldItemId() {
        return this.itemId;
    }
}
