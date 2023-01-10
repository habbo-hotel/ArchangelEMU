package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class CatalogLimitedConfiguration implements Runnable {
    private final int itemId;
    private LinkedBlockingQueue<Integer> limitedNumbers;
    @Getter
    @Setter
    private int totalSet;
    private final Object lock = new Object();

    public CatalogLimitedConfiguration(int itemId, LinkedList<Integer> availableNumbers, int totalSet) {
        this.itemId = itemId;
        this.totalSet = totalSet;
        LinkedList<Integer> numbers = new LinkedList<>(availableNumbers);

        if (Emulator.getConfig().getBoolean("catalog.ltd.random", true)) {
            Collections.shuffle(numbers);
        } else {
            Collections.reverse(numbers);
        }
        limitedNumbers = new LinkedBlockingQueue<>(numbers);
    }

    public int getNumber() {
        synchronized (lock) {
            int num = 0;
            try
            {
                num = limitedNumbers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.limitedNumbers.isEmpty()) {
                Emulator.getGameEnvironment().getCatalogManager().moveCatalogItem(Emulator.getGameEnvironment().getCatalogManager().getCatalogItem(this.itemId), Emulator.getConfig().getInt("catalog.ltd.page.soldout"));
            }
            return num;
        }
    }

    public void limitedSold(int catalogItemId, Habbo habbo, HabboItem item) {
        synchronized (lock) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE catalog_items_limited SET user_id = ?, timestamp = ?, item_id = ? WHERE catalog_item_id = ? AND number = ? AND user_id = 0 LIMIT 1")) {
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, Emulator.getIntUnixTimestamp());
                statement.setInt(3, item.getId());
                statement.setInt(4, catalogItemId);
                statement.setInt(5, item.getLimitedSells());
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }

    public void generateNumbers(int starting, int amount) {
        synchronized (lock) {
            LinkedList<Integer> numbers = new LinkedList<>();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO catalog_items_limited (catalog_item_id, number) VALUES (?, ?)")) {
                statement.setInt(1, this.itemId);

                for (int i = starting; i <= amount; i++) {
                    statement.setInt(2, i);
                    statement.addBatch();
                    numbers.push(i);
                }

                statement.executeBatch();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }

            this.totalSet += amount;

            if (Emulator.getConfig().getBoolean("catalog.ltd.random", true)) {
                Collections.shuffle(numbers);
            } else {
                Collections.reverse(numbers);
            }

            limitedNumbers = new LinkedBlockingQueue<>(numbers);
        }

    }

    public int available() {
        return this.limitedNumbers.size();
    }


    @Override
    public void run() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE catalog_items SET limited_stack = ?, limited_sells = ? WHERE id = ?")) {
            statement.setInt(1, this.totalSet);
            statement.setInt(2, this.totalSet - this.available());
            statement.setInt(3, this.itemId);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }
}