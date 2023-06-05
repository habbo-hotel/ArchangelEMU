package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.CatalogPageLayouts;
import com.eu.habbo.habbohotel.catalog.layouts.RoomBundleLayout;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class RoomBundleCommand extends Command {
    public RoomBundleCommand() {
        super("cmd_bundle");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        int parentId;
        int credits;
        int points;
        int pointsType;

        if (params.length < 5) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_bundle.missing_params"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (Emulator.getGameEnvironment().getCatalogManager().getCatalogPage("room_bundle_" + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId()) != null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_bundle.duplicate"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        parentId = Integer.parseInt(params[1]);
        credits = Integer.parseInt(params[2]);
        points = Integer.parseInt(params[3]);
        pointsType = Integer.parseInt(params[4]);

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().createCatalogPage("Room Bundle: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getName(), "room_bundle_" + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), 0, CatalogPageLayouts.room_bundle, gameClient.getHabbo().getHabboInfo().getPermissionGroup().getId(), parentId);

        if (page instanceof RoomBundleLayout roomBundleLayout) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO catalog_items (page_id, item_ids, catalog_name, cost_credits, cost_points, points_type ) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, roomBundleLayout.getId());
                statement.setString(2, "");
                statement.setString(3, "room_bundle");
                statement.setInt(4, credits);
                statement.setInt(5, points);
                statement.setInt(6, pointsType);
                statement.execute();

                try (ResultSet set = statement.getGeneratedKeys()) {
                    if (set.next()) {
                        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM catalog_items WHERE id = ?")) {
                            stmt.setInt(1, set.getInt(1));
                            try (ResultSet st = stmt.executeQuery()) {
                                if (st.next()) {
                                    page.addItem(new CatalogItem(st));
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
            roomBundleLayout.loadItems(gameClient.getHabbo().getHabboInfo().getCurrentRoom());

            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_bundle").replace("%id%", roomBundleLayout.getId() + ""), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
