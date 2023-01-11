package com.eu.habbo.habbohotel.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.catalog.marketplace.GetMarketplaceOffersEvent;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceBuyOfferResultComposer;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceCancelOfferResultComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemCancelledEvent;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemOfferedEvent;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemSoldEvent;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;


@Slf4j

public class MarketPlace {

    //Configuration. Loaded from database & updated accordingly.
    public static boolean MARKETPLACE_ENABLED = true;

    //Currency to use.
    public static int MARKETPLACE_CURRENCY = 0;

    private MarketPlace() {
    }


    public static THashSet<MarketPlaceOffer> getOwnOffers(Habbo habbo) {
        THashSet<MarketPlaceOffer> offers = new THashSet<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT items_base.type AS type, items.item_id AS base_item_id, items.limited_data AS ltd_data, marketplace_items.* FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE marketplace_items.user_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    offers.add(new MarketPlaceOffer(set, true));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return offers;
    }


    public static void takeBackItem(Habbo habbo, int offerId) {
        MarketPlaceOffer offer = habbo.getInventory().getOffer(offerId);

        if (!Emulator.getPluginManager().fireEvent(new MarketPlaceItemCancelledEvent(offer)).isCancelled()) {
            takeBackItem(habbo, offer);
        }
    }


    private static void takeBackItem(Habbo habbo, MarketPlaceOffer offer) {
        if (offer != null && habbo.getInventory().getMarketplaceItems().contains(offer)) {
            GetMarketplaceOffersEvent.cachedResults.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                try (PreparedStatement ownerCheck = connection.prepareStatement("SELECT user_id FROM marketplace_items WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    ownerCheck.setInt(1, offer.getOfferId());
                    try (ResultSet ownerSet = ownerCheck.executeQuery()) {
                        ownerSet.last();

                        if (ownerSet.getRow() == 0) {
                            return;
                        }

                        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM marketplace_items WHERE id = ? AND state != 2")) {
                            statement.setInt(1, offer.getOfferId());
                            int count = statement.executeUpdate();

                            if (count != 0) {
                                habbo.getInventory().removeMarketplaceOffer(offer);
                                try (PreparedStatement updateItems = connection.prepareStatement("UPDATE items SET user_id = ? WHERE id = ? LIMIT 1")) {
                                    updateItems.setInt(1, habbo.getHabboInfo().getId());
                                    updateItems.setInt(2, offer.getSoldItemId());
                                    updateItems.execute();

                                    try (PreparedStatement selectItem = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1")) {
                                        selectItem.setInt(1, offer.getSoldItemId());
                                        try (ResultSet set = selectItem.executeQuery()) {
                                            while (set.next()) {
                                                HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);
                                                habbo.getInventory().getItemsComponent().addItem(item);
                                                habbo.getClient().sendResponse(new MarketplaceCancelOfferResultComposer(offer, true));
                                                habbo.getClient().sendResponse(new UnseenItemsComposer(item));
                                                habbo.getClient().sendResponse(new FurniListInvalidateComposer());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
                habbo.getClient().sendResponse(new MarketplaceCancelOfferResultComposer(offer, false));
            }
        }
    }


    public static List<MarketPlaceOffer> getOffers(int minPrice, int maxPrice, String search, int sort) {
        List<MarketPlaceOffer> offers = new ArrayList<>(10);
        String query = "SELECT B.* FROM marketplace_items a INNER JOIN (SELECT b.item_id AS base_item_id, b.limited_data AS ltd_data, marketplace_items.*, AVG(price) as avg, MIN(marketplace_items.price) as minPrice, MAX(marketplace_items.price) as maxPrice, COUNT(*) as number, (SELECT COUNT(*) FROM marketplace_items c INNER JOIN items as items_b ON c.item_id = items_b.id WHERE state = 2 AND items_b.item_id = base_item_id AND DATE(from_unixtime(sold_timestamp)) = CURDATE()) as sold_count_today FROM marketplace_items INNER JOIN items b ON marketplace_items.item_id = b.id INNER JOIN items_base bi ON b.item_id = bi.id INNER JOIN catalog_items ci ON bi.id = ci.item_ids WHERE price = (SELECT MIN(e.price) FROM marketplace_items e, items d WHERE e.item_id = d.id AND d.item_id = b.item_id AND e.state = 1 AND e.timestamp > ? GROUP BY d.item_id) AND state = 1 AND timestamp > ?";
        if (minPrice > 0) {
            query += " AND CEIL(price + (price / 100)) >= " + minPrice;
        }
        if (maxPrice > 0 && maxPrice > minPrice) {
            query += " AND CEIL(price + (price / 100)) <= " + maxPrice;
        }
        if (search.length() > 0) {
            query += " AND ( bi.public_name LIKE ? OR ci.catalog_name LIKE ? ) ";
        }

        query += " GROUP BY base_item_id, ltd_data";

        switch (sort) {
            case 6:
                query += " ORDER BY number ASC";
                break;
            case 5:
                query += " ORDER BY number DESC";
                break;
            case 4:
                query += " ORDER BY sold_count_today ASC";
                break;
            case 3:
                query += " ORDER BY sold_count_today DESC";
                break;
            case 2:
                query += " ORDER BY minPrice ASC";
                break;
            default:
            case 1:
                query += " ORDER BY minPrice DESC";
                break;
        }

        query += ")";

        query += " AS B ON a.id = B.id";

        query += " LIMIT 250";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 172800);
            statement.setInt(2, Emulator.getIntUnixTimestamp() - 172800);
            if (search.length() > 0) {
                statement.setString(3, "%" + search + "%");
                statement.setString(4, "%" + search + "%");
            }

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    offers.add(new MarketPlaceOffer(set, false));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return offers;
    }


    public static void serializeItemInfo(int itemId, ServerMessage message) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT avg(marketplace_items.price) as price, COUNT(*) as sold, (datediff(NOW(), DATE(from_unixtime(marketplace_items.timestamp)))) as day FROM marketplace_items INNER JOIN items ON items.id = marketplace_items.item_id INNER JOIN items_base ON items.item_id = items_base.id WHERE items.limited_data = '0:0' AND marketplace_items.state = 2 AND items_base.sprite_id = ? AND DATE(from_unixtime(marketplace_items.timestamp)) >= NOW() - INTERVAL 30 DAY GROUP BY DATE(from_unixtime(marketplace_items.timestamp))", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setInt(1, itemId);

            message.appendInt(avarageLastXDays(itemId, 7));
            message.appendInt(itemsOnSale(itemId));
            message.appendInt(30);

            try (ResultSet set = statement.executeQuery()) {
                set.last();
                message.appendInt(set.getRow());
                set.beforeFirst();

                while (set.next()) {
                    message.appendInt(-set.getInt("day"));
                    message.appendInt(MarketPlace.calculateCommision(set.getInt("price")));
                    message.appendInt(set.getInt("sold"));
                }
            }

            message.appendInt(1);
            message.appendInt(itemId);
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }


    public static int itemsOnSale(int baseItemId) {
        int number = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as number, AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 1 AND timestamp >= ? AND items_base.sprite_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 172800);
            statement.setInt(2, baseItemId);
            try (ResultSet set = statement.executeQuery()) {
                set.first();
                number = set.getInt("number");
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return number;
    }


    private static int avarageLastXDays(int baseItemId, int days) {
        int avg = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 2 AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL ? DAY AND items_base.sprite_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setInt(1, days);
            statement.setInt(2, baseItemId);

            try (ResultSet set = statement.executeQuery()) {
                set.first();
                avg = set.getInt("avg");
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return calculateCommision(avg);
    }


    public static void buyItem(int offerId, GameClient client) {
        GetMarketplaceOffersEvent.cachedResults.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM marketplace_items WHERE id = ? LIMIT 1")) {
            statement.setInt(1, offerId);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    try (PreparedStatement itemStatement = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                        itemStatement.setInt(1, set.getInt("item_id"));
                        try (ResultSet itemSet = itemStatement.executeQuery()) {
                            itemSet.first();

                            if (itemSet.getRow() <= 0) {
                                return;
                            }

                            int price = MarketPlace.calculateCommision(set.getInt("price"));
                            if (set.getInt("state") != 1) {
                                sendErrorMessage(client, set.getInt("item_id"), offerId);
                            } else if ((MARKETPLACE_CURRENCY == 0 && price > client.getHabbo().getHabboInfo().getCredits()) || (MARKETPLACE_CURRENCY > 0 && price > client.getHabbo().getHabboInfo().getCurrencyAmount(MARKETPLACE_CURRENCY))) {
                                client.sendResponse(new MarketplaceBuyOfferResultComposer(MarketplaceBuyOfferResultComposer.NOT_ENOUGH_CREDITS, 0, offerId, price));
                            } else {
                                try (PreparedStatement updateOffer = connection.prepareStatement("UPDATE marketplace_items SET state = 2, sold_timestamp = ? WHERE id = ?")) {
                                    updateOffer.setInt(1, Emulator.getIntUnixTimestamp());
                                    updateOffer.setInt(2, offerId);
                                    updateOffer.execute();
                                }
                                Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(set.getInt(DatabaseConstants.USER_ID));
                                HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(itemSet);

                                MarketPlaceItemSoldEvent event = new MarketPlaceItemSoldEvent(habbo, client.getHabbo(), item, set.getInt("price"));
                                if (Emulator.getPluginManager().fireEvent(event).isCancelled()) {
                                    return;
                                }
                                event.price = calculateCommision(event.price);

                                item.setUserId(client.getHabbo().getHabboInfo().getId());
                                item.needsUpdate(true);
                                Emulator.getThreading().run(item);

                                client.getHabbo().getInventory().getItemsComponent().addItem(item);

                                if (MARKETPLACE_CURRENCY == 0) {
                                    client.getHabbo().giveCredits(-event.price);
                                } else {
                                    client.getHabbo().givePoints(MARKETPLACE_CURRENCY, -event.price);
                                }

                                client.sendResponse(new CreditBalanceComposer(client.getHabbo()));
                                client.sendResponse(new UnseenItemsComposer(item));
                                client.sendResponse(new FurniListInvalidateComposer());
                                client.sendResponse(new MarketplaceBuyOfferResultComposer(MarketplaceBuyOfferResultComposer.REFRESH, 0, offerId, price));

                                if (habbo != null) {
                                    habbo.getInventory().getOffer(offerId).setState(MarketPlaceState.SOLD);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }


    public static void sendErrorMessage(GameClient client, int baseItemId, int offerId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT marketplace_items.*, COUNT( * ) AS count\n" +
                """
                        FROM marketplace_items
                        INNER JOIN items ON marketplace_items.item_id = items.id
                        INNER JOIN items_base ON items.item_id = items_base.id
                        WHERE items_base.sprite_id = (
                        SELECT items_base.sprite_id
                        FROM items_base
                        WHERE items_base.id = ? LIMIT 1)
                        ORDER BY price
                        LIMIT 1""", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setInt(1, baseItemId);
            try (ResultSet countSet = statement.executeQuery()) {
                countSet.last();
                if (countSet.getRow() == 0)
                    client.sendResponse(new MarketplaceBuyOfferResultComposer(MarketplaceBuyOfferResultComposer.SOLD_OUT, 0, offerId, 0));
                else {
                    countSet.first();
                    client.sendResponse(new MarketplaceBuyOfferResultComposer(MarketplaceBuyOfferResultComposer.UPDATES, countSet.getInt("count"), countSet.getInt("id"), MarketPlace.calculateCommision(countSet.getInt("price"))));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }


    public static boolean sellItem(GameClient client, HabboItem item, int price) {
        if (item == null || client == null)
            return false;

        if (!item.getBaseItem().allowMarketplace() || price < 0)
            return false;

        MarketPlaceItemOfferedEvent event = new MarketPlaceItemOfferedEvent(client.getHabbo(), item, price);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            return false;
        }

        GetMarketplaceOffersEvent.cachedResults.clear();

        client.sendResponse(new FurniListRemoveComposer(event.getItem().getGiftAdjustedId()));
        client.sendResponse(new FurniListInvalidateComposer());

        event.getItem().setFromGift(false);

        MarketPlaceOffer offer = new MarketPlaceOffer(event.getItem(), event.getPrice(), client.getHabbo());
        client.getHabbo().getInventory().addMarketplaceOffer(offer);
        client.getHabbo().getInventory().getItemsComponent().removeHabboItem(event.getItem());
        item.setUserId(-1);
        item.needsUpdate(true);
        Emulator.getThreading().run(item);

        return true;
    }


    public static void getCredits(GameClient client) {
        int credits = 0;

        THashSet<MarketPlaceOffer> offers = new THashSet<>();
        offers.addAll(client.getHabbo().getInventory().getMarketplaceItems());

        for (MarketPlaceOffer offer : offers) {
            if (offer.getState().equals(MarketPlaceState.SOLD)) {
                client.getHabbo().getInventory().removeMarketplaceOffer(offer);
                credits += offer.getPrice();
                removeUser(offer);
                offer.setNeedsUpdate(true);
                Emulator.getThreading().run(offer);
            }
        }

        offers.clear();

        if (MARKETPLACE_CURRENCY == 0) {
            client.getHabbo().giveCredits(credits);
        } else {
            client.getHabbo().givePoints(MARKETPLACE_CURRENCY, credits);
        }
        client.sendResponse(new CreditBalanceComposer(client.getHabbo()));
    }

    private static void removeUser(MarketPlaceOffer offer) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE marketplace_items SET user_id = ? WHERE id = ?")) {
            statement.setInt(1, -1);
            statement.setInt(2, offer.getOfferId());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }


    public static int calculateCommision(int price) {
        return price + (int) Math.ceil(price / 100.0);
    }
}
