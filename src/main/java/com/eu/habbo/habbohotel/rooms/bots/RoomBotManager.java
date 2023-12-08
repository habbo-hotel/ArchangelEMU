package com.eu.habbo.habbohotel.rooms.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitManager;
import com.eu.habbo.habbohotel.rooms.bots.entities.RoomBot;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomUnitSubManager;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.generic.alerts.BotErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.BotAddedToInventoryComposer;
import com.eu.habbo.messages.outgoing.inventory.BotRemovedFromInventoryComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.bots.BotPickUpEvent;
import com.eu.habbo.plugin.events.bots.BotPlacedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;
import static com.eu.habbo.habbohotel.rooms.Room.CAUGHT_EXCEPTION;


@Slf4j
public class RoomBotManager extends RoomUnitSubManager {
    private final RoomUnitManager roomUnitManager;

    @Getter
    private final ConcurrentHashMap<Integer, Bot> currentBots;

    public RoomBotManager(RoomUnitManager roomUnitManager) {
        super(roomUnitManager);
        this.roomUnitManager = roomUnitManager;
        currentBots = new ConcurrentHashMap<>();
    }

    public synchronized void loadBots(Connection connection) {
        this.currentBots.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON bots.owner_id = users.id WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Bot bot = Emulator.getGameEnvironment().getBotManager().loadBot(set);

                    if (bot != null) {
                        bot.getRoomUnit().setRoom(this.room);

                        RoomTile spawnTile = this.room.getLayout().getTile((short) set.getInt("x"), (short) set.getInt("y"));

                        if (spawnTile == null) {
                            bot.getRoomUnit().setCanWalk(false);
                        } else {
                            if (spawnTile.getState().equals(RoomTileState.INVALID)) {
                                bot.getRoomUnit().setCanWalk(false);
                            }

                            bot.getRoomUnit().setCurrentPosition(spawnTile);
                            bot.getRoomUnit().setCurrentZ(set.getDouble("z"));
                            bot.getRoomUnit().setRotation(RoomRotation.values()[set.getInt("rot")]);
                            bot.getRoomUnit().setSpawnTile(spawnTile);
                            bot.getRoomUnit().setSpawnHeight(spawnTile.getState().equals(RoomTileState.INVALID) ? 0 : spawnTile.getStackHeight());
                        }

                        bot.getRoomUnit().setDanceType(DanceType.values()[set.getInt("dance")]);

                        bot.getRoomUnit().giveEffect(set.getInt("effect"), Integer.MAX_VALUE, false);

                        this.addBot(bot);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
    }

    public Bot getRoomBotById(int botId) {
        return this.currentBots.get(botId);
    }

    public List<Bot> getBotsByName(String name) {
        synchronized (this.currentBots) {
            return currentBots.values().stream().filter(bot -> bot.getName().equalsIgnoreCase(name)).toList();
        }
    }

    public Bot getBotByRoomUnit(RoomUnit roomUnit) {
        return this.currentBots.values().stream().filter(bot -> bot.getRoomUnit() == roomUnit).findFirst().orElse(null);
    }

    public boolean hasBotsAt(RoomTile tile) {
        return this.currentBots.values().stream().anyMatch(bot -> bot.getRoomUnit().getCurrentPosition().equals(tile));
    }

    public Collection<Bot> getBotsAt(RoomTile tile) {
        return this.currentBots.values().stream().filter(bot -> bot.getRoomUnit().getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public void placeBot(Bot bot, Habbo botOwner, int x, int y) {
        synchronized (this.currentBots) {
            RoomTile spawnTile = room.getLayout().getTile((short) x, (short) y);

            if (spawnTile == null || (!spawnTile.isWalkable() && !this.room.canSitOrLayAt(spawnTile)) || roomUnitManager.areRoomUnitsAt(spawnTile)) {
                botOwner.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_BOTS_SELECTED_TILE_NOT_FREE));
                return;
            }

            if (Emulator.getPluginManager().isRegistered(BotPlacedEvent.class, false)) {
                Event event = new BotPlacedEvent(bot, spawnTile, botOwner);
                Emulator.getPluginManager().fireEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }

            if (this.currentBots.size() >= Room.MAXIMUM_BOTS && !botOwner.hasPermissionRight(Permission.ACC_UNLIMITED_BOTS)) {
                botOwner.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_MAX_BOTS));
                return;
            }

            RoomBot roomBot = bot.getRoomUnit();

            roomBot.setRoom(this.room);
            roomBot.setCurrentPosition(spawnTile);
            roomBot.setCurrentZ(spawnTile.getStackHeight());
            roomBot.setRotation(RoomRotation.SOUTH);
            roomBot.setSpawnTile(spawnTile);
            roomBot.setSpawnHeight(spawnTile.getState().equals(RoomTileState.INVALID) ? 0 : spawnTile.getStackHeight());

            bot.setSqlUpdateNeeded(true);
            Emulator.getThreading().run(bot);

            this.addBot(bot);

            this.room.sendComposer(new RoomUsersComposer(bot).compose());

            roomBot.instantUpdate();

            botOwner.getInventory().getBotsComponent().removeBot(bot);
            botOwner.getClient().sendResponse(new BotRemovedFromInventoryComposer(bot));
            bot.onPlace(botOwner, room);
        }
    }

    public void updateBotsAt(RoomTile tile) {
        Collection<Bot> bots = this.getBotsAt(tile);

        if (bots == null || bots.isEmpty()) {
            return;
        }

        RoomItem item = this.room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());

        bots.forEach(bot -> {
            double z = bot.getRoomUnit().getCurrentPosition().getStackHeight();

            if (bot.getRoomUnit().hasStatus(RoomUnitStatus.SIT) && ((item == null && !bot.getRoomUnit().isCmdSitEnabled()) || (item != null && !item.getBaseItem().allowSit()))) {
                bot.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
            }

            if (bot.getRoomUnit().hasStatus(RoomUnitStatus.LAY) && ((item == null && !bot.getRoomUnit().isCmdLayEnabled()) || (item != null && !item.getBaseItem().allowLay()))) {
                bot.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
            }

            if (item != null && (item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) {
                if (item.getBaseItem().allowSit()) {
                    bot.getRoomUnit().addStatus(RoomUnitStatus.SIT, String.valueOf(Item.getCurrentHeight(item)));
                } else if (item.getBaseItem().allowLay()) {
                    bot.getRoomUnit().addStatus(RoomUnitStatus.LAY, String.valueOf(Item.getCurrentHeight(item)));
                }

                bot.getRoomUnit().setCurrentZ(item.getCurrentZ());
                bot.getRoomUnit().setRotation(RoomRotation.fromValue(item.getRotation()));
            } else {
                bot.getRoomUnit().setCurrentZ(z);
            }
        });
    }

    public void pickUpBot(Bot bot, Habbo picker) {
        HabboInfo botOwnerInfo = picker == null ? bot.getOwnerInfo() : picker.getHabboInfo();

        BotPickUpEvent pickedUpEvent = new BotPickUpEvent(bot, picker);
        Emulator.getPluginManager().fireEvent(pickedUpEvent);

        if (pickedUpEvent.isCancelled())
            return;

        if (picker == null || (bot.getOwnerInfo().getId() == picker.getHabboInfo().getId() || picker.hasPermissionRight(Permission.ACC_ANYROOMOWNER))) {
            if (picker != null && !picker.hasPermissionRight(Permission.ACC_UNLIMITED_BOTS) && picker.getInventory().getBotsComponent().getBots().size() >= BotManager.MAXIMUM_BOT_INVENTORY_SIZE) {
                picker.alert(Emulator.getTexts().getValue("error.bots.max.inventory").replace("%amount%", String.valueOf(BotManager.MAXIMUM_BOT_INVENTORY_SIZE)));
                return;
            }

            bot.onPickUp(picker, this.room);

            bot.setFollowingHabboId(0);

            //@DEPRECATED
            bot.setOwnerId(botOwnerInfo.getId());
            bot.setOwnerName(botOwnerInfo.getUsername());

            bot.setOwnerInfo(botOwnerInfo);

            this.removeBot(bot);

            bot.setSqlUpdateNeeded(true);
            Emulator.getThreading().run(bot);

            Habbo receiver = picker == null ? Emulator.getGameEnvironment().getHabboManager().getHabbo(botOwnerInfo.getId()) : picker;

            if (receiver != null) {
                receiver.getInventory().getBotsComponent().addBot(bot);
                receiver.getClient().sendResponse(new BotAddedToInventoryComposer(bot));
            }
        }
    }


    public void addBot(Bot unit) {
        currentBots.put(unit.getId(), unit);
    }

    public void removeBot(Bot bot) {
        if (this.currentBots.containsKey(bot.getId())) {
            //TODO gotta do a method to removeUnit and clear tile
            if (bot.getRoomUnit().getCurrentPosition() != null) {
                bot.getRoomUnit().getCurrentPosition().removeUnit(bot.getRoomUnit());
            }

            this.currentBots.remove(bot.getId());
            roomUnitManager.removeUnit(bot.getRoomUnit().getVirtualId());


            bot.getRoomUnit().setRoom(null);

            this.room.sendComposer(new UserRemoveMessageComposer(bot.getRoomUnit()).compose());
        }
    }

    public void clear() {
        currentBots.clear();
    }

    public void dispose() {
        Iterator<Bot> botIterator = this.currentBots.values().iterator();

        while (botIterator.hasNext()) {
            try {
                Bot bot = botIterator.next();
                bot.setSqlUpdateNeeded(true);
                Emulator.getThreading().run(bot);
            } catch (NoSuchElementException e) {
                log.error("Caught Exception", e);
                break;
            }
        }

        this.currentBots.clear();

    }

    @Override
    public List<RoomBot> cycle() {
        List<RoomBot> updatedBots = new ArrayList<>();
        if (!getCurrentBots().isEmpty()) {
            Iterator<Bot> botIterator = getCurrentBots().values().iterator();

            while(botIterator.hasNext()) {
                try {
                    final Bot bot;
                    try {
                        bot = botIterator.next();
                    } catch (Exception e) {
                        break;
                    }

                    if (!room.isAllowBotsWalk() && bot.getRoomUnit().isWalking()) {
                        bot.getRoomUnit().stopWalking();
                        updatedBots.add(bot.getRoomUnit());
                        continue;
                    }

                    bot.getRoomUnit().cycle();

                    if(bot.getRoomUnit().isStatusUpdateNeeded()) {
                        bot.getRoomUnit().setStatusUpdateNeeded(false);
                        updatedBots.add(bot.getRoomUnit());
                    }


                } catch (NoSuchElementException e) {
                    log.error(CAUGHT_EXCEPTION, e);
                    break;
                }
            }
        }


        return updatedBots;
    }
}
