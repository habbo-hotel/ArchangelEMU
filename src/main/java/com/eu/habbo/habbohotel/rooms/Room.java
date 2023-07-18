package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.VisitorBot;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.pets.*;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.entities.RoomEntity;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.HabboGroupDetailsMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionAnsweredComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionComposer;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.messages.outgoing.rooms.items.*;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.RemainingMutePeriodComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.*;
import com.eu.habbo.plugin.events.rooms.RoomLoadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadingEvent;
import com.eu.habbo.plugin.events.users.*;
import com.eu.habbo.threading.runnables.YouAreAPirate;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
public class Room implements Comparable<Room>, ISerialize, Runnable {
    @Getter
    private final RoomInfo roomInfo;
    @Getter
    private final RoomUnitManager roomUnitManager;
    @Getter
    private final RoomItemManager roomItemManager;
    private TraxManager traxManager;
    private static final String CAUGHT_EXCEPTION = "Caught exception";
    public static final Comparator<Room> SORT_SCORE = (o1, o2) -> o2.roomInfo.getScore() - o1.roomInfo.getScore();
    public static final Comparator<Room> SORT_ID = (o1, o2) -> o2.roomInfo.getId() - o1.roomInfo.getId();
    //Configuration. Loaded from database & updated accordingly.
    public static boolean HABBO_CHAT_DELAY = false;
    public static int MAXIMUM_BOTS = 10;
    public static int MAXIMUM_PETS = 10;
    public static int MAXIMUM_FURNI = 2500;
    public static int MAXIMUM_POSTITNOTES = 200;
    public static int HAND_ITEM_TIME = 10;
    public static int IDLE_CYCLES = 240;
    public static int IDLE_CYCLES_KICK = 480;
    public static String PREFIX_FORMAT = "[<font color=\"%color%\">%prefix%</font>] ";
    public static int ROLLERS_MAXIMUM_ROLL_AVATARS = 1;
    public static boolean MUTEAREA_CAN_WHISPER = false;
    public static final double MAXIMUM_FURNI_HEIGHT = 40d;
    public final ConcurrentHashMap<RoomTile, THashSet<RoomItem>> tileCache = new ConcurrentHashMap<>();
    public final List<Integer> userVotes;
    private final TIntObjectMap<Habbo> habboQueue = TCollections.synchronizedMap(new TIntObjectHashMap<>(0));
    private final THashSet<RoomTrade> activeTrades;
    private final TIntArrayList rights;
    private final TIntIntHashMap mutedHabbos;
    private final TIntObjectHashMap<RoomBan> bannedHabbos;
    private final ConcurrentSet<Game> games;
    private final TIntObjectMap<String> furniOwnerNames;
    private final TIntIntMap furniOwnerCount;
    private final THashSet<String> wordFilterWords;
    private final TIntObjectMap<RoomItem> roomItems;
    private final Object loadLock = new Object();
    //Use appropriately. Could potentially cause memory leaks when used incorrectly.
    public volatile boolean preventUnloading = false;
    public volatile boolean preventUncaching = false;
    public final ConcurrentHashMap.KeySetView<ServerMessage, Boolean> scheduledComposers = ConcurrentHashMap.newKeySet();
    public ConcurrentHashMap.KeySetView<Runnable, Boolean> scheduledTasks = ConcurrentHashMap.newKeySet();
    private String wordQuiz = "";
    private int noVotes = 0;
    private int yesVotes = 0;
    private int wordQuizEnd = 0;
    public ScheduledFuture<?> roomCycleTask;
    private RoomLayout layout;
    private final String layoutName;
    private volatile boolean allowBotsWalk;
    private volatile boolean allowEffects;
    private RoomPromotion promotion;
    private volatile boolean needsUpdate;
    private volatile boolean loaded;
    private volatile boolean preLoaded;
    private int roomIdleCycles;
    private final int muteTime = Emulator.getConfig().getInt("hotel.flood.mute.time", 30);
    private long rollerCycle = System.currentTimeMillis();
    private volatile int lastTimerReset = Emulator.getIntUnixTimestamp();
    private volatile boolean muted;
    private RoomSpecialTypes roomSpecialTypes;
    private boolean cycleOdd;
    private long cycleTimestamp;
    final HashMap<RoomTile, InteractionWiredTrigger> triggersOnRoom;

    public Room(ResultSet set) throws SQLException {
        this.roomInfo = new RoomInfo(set);
        this.roomUnitManager = new RoomUnitManager(this.roomInfo.getId());
        this.roomItemManager = new RoomItemManager();

        this.layoutName = set.getString("model");
        this.bannedHabbos = new TIntObjectHashMap<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_promotions WHERE room_id = ? AND end_timestamp > ? LIMIT 1")) {
            if (this.roomInfo.isPromoted()) {
                statement.setInt(1, this.roomInfo.getId());
                statement.setInt(2, Emulator.getIntUnixTimestamp());

                try (ResultSet promotionSet = statement.executeQuery()) {
                    this.roomInfo.setPromoted(false);
                    if (promotionSet.next()) {
                        this.roomInfo.setPromoted(true);
                        this.promotion = new RoomPromotion(this, promotionSet);
                    }
                }
            }

            this.loadBans(connection);
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        this.preLoaded = true;
        this.allowBotsWalk = true;
        this.allowEffects = true;
        this.furniOwnerNames = TCollections.synchronizedMap(new TIntObjectHashMap<>(0));
        this.furniOwnerCount = TCollections.synchronizedMap(new TIntIntHashMap(0));
        this.roomItems = TCollections.synchronizedMap(new TIntObjectHashMap<>(0));
        this.wordFilterWords = new THashSet<>(0);

        this.mutedHabbos = new TIntIntHashMap();
        this.games = new ConcurrentSet<>();

        this.activeTrades = new THashSet<>(0);
        this.rights = new TIntArrayList();
        this.userVotes = new ArrayList<>();

        this.triggersOnRoom = new HashMap<>();
    }

    public synchronized void loadData() {
        synchronized (this.loadLock) {
            if (!this.preLoaded || this.loaded)
                return;

            this.preLoaded = false;

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                this.roomUnitManager.clear();

                this.roomSpecialTypes = new RoomSpecialTypes();

                this.loadLayout();
                this.loadRights(connection);
                this.loadItems(connection);
                this.loadHeightmap();
                this.roomUnitManager.load(connection);
                this.loadWordFilter(connection);

                this.roomIdleCycles = 0;
                this.loaded = true;

                this.roomCycleTask = Emulator.getThreading().getService().scheduleAtFixedRate(this, 500, 500, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION, e);
            }

            this.traxManager = new TraxManager(this);

            if (this.roomInfo.isJukeboxEnabled()) {
                this.traxManager.play(0);
                for (RoomItem item : this.roomSpecialTypes.getItemsOfType(InteractionJukeBox.class)) {
                    this.updateItem(item.setExtradata("1"));
                }
            }

            for (RoomItem item : this.roomSpecialTypes.getItemsOfType(InteractionFireworks.class)) {
                this.updateItem(item.setExtradata("1"));
            }
        }

        Emulator.getPluginManager().fireEvent(new RoomLoadedEvent(this));
    }

    private synchronized void loadLayout() {
        try {
            if (this.layout == null) {
                if (this.roomInfo.isModelOverridden()) {
                    this.layout = Emulator.getGameEnvironment().getRoomManager().loadCustomLayout(this);
                } else {
                    this.layout = Emulator.getGameEnvironment().getRoomManager().loadLayout(this.layoutName, this);
                }
            }
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }
    }

    private synchronized void loadHeightmap() {
        try {
            if (this.layout != null) {
                for (short x = 0; x < this.layout.getMapSizeX(); x++) {
                    for (short y = 0; y < this.layout.getMapSizeY(); y++) {
                        RoomTile tile = this.layout.getTile(x, y);
                        if (tile != null) {
                            this.updateTile(tile);
                        }
                    }
                }
            } else {
                log.error("Unknown Room Layout for Room (ID: {})", this.roomInfo.getId());
            }
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }
    }

    private synchronized void loadItems(Connection connection) {
        this.roomItems.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ?")) {
            statement.setInt(1, this.roomInfo.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.addHabboItem(Emulator.getGameEnvironment().getItemManager().loadHabboItem(set));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }

        if (this.itemCount() > Room.MAXIMUM_FURNI) {
            log.error("Room ID: {} has exceeded the furniture limit ({} > {}).", this.roomInfo.getId(), this.itemCount(), Room.MAXIMUM_FURNI);
        }
    }

    private synchronized void loadWordFilter(Connection connection) {
        this.wordFilterWords.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_wordfilter WHERE room_id = ?")) {
            statement.setInt(1, this.roomInfo.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.wordFilterWords.add(set.getString("word"));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }
    }

    public void updateTile(RoomTile tile) {
        if (tile != null) {
            tile.setStackHeight(this.getStackHeight(tile.getX(), tile.getY(), false));
            tile.setState(this.calculateTileState(tile));
        }
    }

    public void updateTiles(THashSet<RoomTile> tiles) {
        for (RoomTile tile : tiles) {
            this.tileCache.remove(tile);
            tile.setStackHeight(this.getStackHeight(tile.getX(), tile.getY(), false));
            tile.setState(this.calculateTileState(tile));
        }

        this.sendComposer(new HeightMapUpdateMessageComposer(this, tiles).compose());
    }

    private RoomTileState calculateTileState(RoomTile tile) {
        return this.calculateTileState(tile, null);
    }

    private RoomTileState calculateTileState(RoomTile tile, RoomItem exclude) {
        if (tile == null || tile.getState() == RoomTileState.INVALID)
            return RoomTileState.INVALID;

        RoomTileState result = RoomTileState.OPEN;
        THashSet<RoomItem> items = this.getItemsAt(tile);

        if (items == null) return RoomTileState.INVALID;

        RoomItem tallestItem = null;

        for (RoomItem item : items) {
            if (exclude != null && item == exclude) continue;

            if (item.getBaseItem().allowLay()) {
                return RoomTileState.LAY;
            }

            if (tallestItem != null && tallestItem.getZ() + Item.getCurrentHeight(tallestItem) > item.getZ() + Item.getCurrentHeight(item))
                continue;

            result = this.checkStateForItem(item, tile);
            tallestItem = item;
        }

        return result;
    }

    private RoomTileState checkStateForItem(RoomItem item, RoomTile tile) {
        RoomTileState result = RoomTileState.BLOCKED;

        if (item.isWalkable()) {
            result = RoomTileState.OPEN;
        } else if (item.getBaseItem().allowSit()) {
            result = RoomTileState.SIT;
        } else if (item.getBaseItem().allowLay()) {
            result = RoomTileState.LAY;
        }

        RoomTileState overriddenState = item.getOverrideTileState(tile, this);
        if (overriddenState != null) {
            result = overriddenState;
        }

        return result;
    }

    public boolean tileWalkable(RoomTile t) {
        return this.tileWalkable(t.getX(), t.getY());
    }

    public boolean tileWalkable(short x, short y) {
        boolean walkable = this.layout.tileWalkable(x, y);
        RoomTile tile = this.getLayout().getTile(x, y);

        if ((walkable && tile != null) && (tile.hasUnits() && !this.roomInfo.isAllowWalkthrough())) {
            walkable = false;
        }

        return walkable;
    }

    public void pickUpItem(RoomItem item, Habbo picker) {
        if (item == null)
            return;

        if (Emulator.getPluginManager().isRegistered(FurniturePickedUpEvent.class, true)) {
            Event furniturePickedUpEvent = new FurniturePickedUpEvent(item, picker);
            Emulator.getPluginManager().fireEvent(furniturePickedUpEvent);

            if (furniturePickedUpEvent.isCancelled())
                return;
        }

        this.removeHabboItem(item.getId());
        item.onPickUp(this);
        item.setRoomId(0);
        item.needsUpdate(true);

        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.sendComposer(new RemoveFloorItemComposer(item).compose());

            THashSet<RoomTile> updatedTiles = new THashSet<>();
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

            for (short x = (short) rectangle.x; x < rectangle.x + rectangle.getWidth(); x++) {
                for (short y = (short) rectangle.y; y < rectangle.y + rectangle.getHeight(); y++) {
                    double stackHeight = this.getStackHeight(x, y, false);
                    RoomTile tile = this.layout.getTile(x, y);

                    if (tile != null) {
                        tile.setStackHeight(stackHeight);
                        updatedTiles.add(tile);
                    }
                }
            }
            this.sendComposer(new HeightMapUpdateMessageComposer(this, updatedTiles).compose());
            this.updateTiles(updatedTiles);
            updatedTiles.forEach(tile -> {
                this.updateHabbosAt(tile);
                this.updateBotsAt(tile.getX(), tile.getY());
            });
        } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
            this.sendComposer(new ItemRemoveMessageComposer(item).compose());
        }

        Habbo habbo = (picker != null && picker.getHabboInfo().getId() == item.getId() ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId()));
        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse(new UnseenItemsComposer(item));
            habbo.getClient().sendResponse(new FurniListInvalidateComposer());
        }
        Emulator.getThreading().run(item);
    }

    public void updateHabbo(Habbo habbo) {
        this.updateRoomUnit(habbo.getRoomUnit());
    }

    public void updateRoomUnit(RoomUnit roomUnit) {
        RoomItem item = this.getTopItemAt(roomUnit.getCurrentPosition().getX(), roomUnit.getCurrentPosition().getY());

        if ((item == null && !roomUnit.isCmdSitEnabled()) || (item != null && !item.getBaseItem().allowSit()))
            roomUnit.removeStatus(RoomUnitStatus.SIT);

        double oldZ = roomUnit.getCurrentZ();

        if (item != null) {
            if (item.getBaseItem().allowSit()) {
                roomUnit.setCurrentZ(item.getZ());
            } else {
                roomUnit.setCurrentZ(item.getZ() + Item.getCurrentHeight(item));
            }

            if (oldZ != roomUnit.getCurrentZ()) {
                this.scheduledTasks.add(() -> {
                    try {
                        item.onWalkOn(roomUnit, Room.this, null);
                    } catch (Exception ignored) {

                    }
                });
            }
        }

        this.sendComposer(new UserUpdateComposer(roomUnit).compose());
    }

    public void updateHabbosAt(RoomTile tile) {
        this.updateHabbosAt(tile.getX(), tile.getY(), new ArrayList<>(this.roomUnitManager.getHabbosAt(tile)));
    }

    public void updateHabbosAt(short x, short y, List<Habbo> habbos) {
        RoomItem item = this.getTopItemAt(x, y);

        for (Habbo habbo : habbos) {

            double oldZ = habbo.getRoomUnit().getCurrentZ();
            RoomRotation oldRotation = habbo.getRoomUnit().getBodyRotation();
            double z = habbo.getRoomUnit().getCurrentPosition().getStackHeight();
            boolean updated = false;

            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT) && ((item == null && !habbo.getRoomUnit().isCmdSitEnabled()) || (item != null && !item.getBaseItem().allowSit()))) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
                updated = true;
            }

            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY) && ((item == null && !habbo.getRoomUnit().isCmdLayEnabled()) || (item != null && !item.getBaseItem().allowLay()))) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
                updated = true;
            }

            if (item != null && (item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) {
                habbo.getRoomUnit().setCurrentZ(item.getZ());
                habbo.getRoomUnit().setPreviousLocationZ(item.getZ());
                habbo.getRoomUnit().setRotation(RoomRotation.fromValue(item.getRotation()));
            } else {
                habbo.getRoomUnit().setCurrentZ(z);
                habbo.getRoomUnit().setPreviousLocationZ(z);
            }

            if (habbo.getRoomUnit().getCurrentPosition().is(x, y) && (oldZ != z || updated || oldRotation != habbo.getRoomUnit().getBodyRotation())) {
                habbo.getRoomUnit().setStatusUpdateNeeded(true);
            }
        }
    }

    public void updateBotsAt(short x, short y) {
        RoomItem topItem = this.getTopItemAt(x, y);

        THashSet<RoomUnit> roomUnits = new THashSet<>();

        RoomTile tile = this.layout.getTile(x, y);
        this.roomUnitManager.getBotsAt(tile).forEach(bot -> {
            if (topItem != null) {
                if (topItem.getBaseItem().allowSit()) {
                    bot.getRoomUnit().setCurrentZ(topItem.getZ());
                    bot.getRoomUnit().setPreviousLocationZ(topItem.getZ());
                    bot.getRoomUnit().setRotation(RoomRotation.fromValue(topItem.getRotation()));
                } else {
                    bot.getRoomUnit().setCurrentZ(topItem.getZ() + Item.getCurrentHeight(topItem));

                    if (topItem.getBaseItem().allowLay()) {
                        bot.getRoomUnit().setStatus(RoomUnitStatus.LAY, (topItem.getZ() + topItem.getBaseItem().getHeight()) + "");
                    }
                }
            } else {
                bot.getRoomUnit().setCurrentZ(bot.getRoomUnit().getCurrentPosition().getStackHeight());
                bot.getRoomUnit().setPreviousLocationZ(bot.getRoomUnit().getCurrentPosition().getStackHeight());
            }
            roomUnits.add(bot.getRoomUnit());
        });

        if (!roomUnits.isEmpty()) {
            this.sendComposer(new UserUpdateComposer(roomUnits).compose());
        }
    }

    public void startTrade(Habbo userOne, Habbo userTwo) {
        RoomTrade trade = new RoomTrade(userOne, userTwo, this);
        synchronized (this.activeTrades) {
            this.activeTrades.add(trade);
        }

        trade.start();
    }

    public void stopTrade(RoomTrade trade) {
        synchronized (this.activeTrades) {
            this.activeTrades.remove(trade);
        }
    }

    public RoomTrade getActiveTradeForHabbo(Habbo user) {
        synchronized (this.activeTrades) {
            for (RoomTrade trade : this.activeTrades) {
                for (RoomTradeUser habbo : trade.getRoomTradeUsers()) {
                    if (habbo.getHabbo() == user)
                        return trade;
                }
            }
        }
        return null;
    }

    public synchronized void dispose() {
        synchronized (this.loadLock) {
            if (this.preventUnloading)
                return;

            if (Emulator.getPluginManager().fireEvent(new RoomUnloadingEvent(this)).isCancelled())
                return;

            if (this.loaded) {
                try {

                    if (this.traxManager != null && !this.traxManager.disposed()) {
                        this.traxManager.dispose();
                    }

                    this.roomCycleTask.cancel(false);
                    this.scheduledTasks.clear();
                    this.scheduledComposers.clear();
                    this.loaded = false;

                    this.tileCache.clear();

                    synchronized (this.mutedHabbos) {
                        this.mutedHabbos.clear();
                    }

                    for (InteractionGameTimer timer : this.getRoomSpecialTypes().getGameTimers().values()) {
                        timer.setRunning(false);
                    }

                    for (Game game : this.games) {
                        game.dispose();
                    }
                    this.games.clear();

                    this.roomUnitManager.removeAllPetsExceptRoomOwner();

                    synchronized (this.roomItems) {
                        TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();


                        for (int i = this.roomItems.size(); i-- > 0; ) {
                            try {
                                iterator.advance();

                                if (iterator.value().needsUpdate())
                                    iterator.value().run();
                            } catch (NoSuchElementException e) {
                                break;
                            }
                        }
                    }

                    if (this.roomSpecialTypes != null) {
                        this.roomSpecialTypes.dispose();
                    }

                    synchronized (this.roomItems) {
                        this.roomItems.clear();
                    }

                    synchronized (this.habboQueue) {
                        this.habboQueue.clear();
                    }

                    this.roomUnitManager.dispose(this);
                } catch (Exception e) {
                    log.error(CAUGHT_EXCEPTION, e);
                }
            }

            try {
                this.wordQuiz = "";
                this.yesVotes = 0;
                this.noVotes = 0;
                this.updateDatabaseUserCount();
                this.preLoaded = true;
                this.layout = null;
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION, e);
            }
        }

        Emulator.getPluginManager().fireEvent(new RoomUnloadedEvent(this));
    }

    @Override
    public int compareTo(Room o) {
        if (o.roomUnitManager.getRoomHabbosCount() != this.roomUnitManager.getRoomHabbosCount()) {
            return o.roomUnitManager.getCurrentRoomHabbos().size() - this.roomUnitManager.getCurrentRoomHabbos().size();
        }

        return this.roomInfo.getId() - o.roomInfo.getId();
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.roomInfo.getId());
        message.appendString(this.roomInfo.getName());

        if (this.roomInfo.isPublicRoom()) {
            message.appendInt(0);
            message.appendString("");
        } else {
            message.appendInt(this.roomInfo.getOwnerInfo().getId());
            message.appendString(this.roomInfo.getOwnerInfo().getUsername());
        }

        message.appendInt(this.roomInfo.getState().ordinal());
        message.appendInt(this.roomUnitManager.getRoomHabbosCount());
        message.appendInt(this.roomInfo.getMaxUsers());
        message.appendString(this.roomInfo.getDescription());
        message.appendInt(this.roomInfo.getTradeMode());
        message.appendInt(this.roomInfo.getScore());
        message.appendInt(0);
        message.appendInt(this.roomInfo.getCategory().getId());

        String[] tags = Arrays.stream(this.roomInfo.getTags().split(";")).filter(t -> !t.isEmpty()).toArray(String[]::new);
        message.appendInt(tags.length);
        for (String s : tags) {
            message.appendString(s);
        }

        int base = 0;

        if (this.roomInfo.hasGuild()) {
            base = base | 2;
        }

        if (this.roomInfo.isPromoted()) {
            base = base | 4;
        }

        if (!this.roomInfo.isPublicRoom()) {
            base = base | 8;
        }

        if (this.roomInfo.isAllowPets()) {
            base = base | 16;
        }

        message.appendInt(base);


        if (this.roomInfo.hasGuild()) {
            message.appendInt(this.roomInfo.getGuild().getId());
            message.appendString(this.roomInfo.getGuild().getName());
            message.appendString(this.roomInfo.getGuild().getBadge());
        }

        if (this.roomInfo.isPromoted()) {
            message.appendString(this.promotion.getTitle());
            message.appendString(this.promotion.getDescription());
            message.appendInt((this.promotion.getEndTimestamp() - Emulator.getIntUnixTimestamp()) / 60);
        }
    }

    @Override
    public void run() {
        synchronized (this.loadLock) {
            if (this.loaded) {
                try {
                    Emulator.getThreading().run(Room.this::cycle);
                } catch (Exception e) {
                    log.error(CAUGHT_EXCEPTION, e);
                }
            }
        }

        this.save();
    }

    public void save() {
        if (this.needsUpdate) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                this.roomInfo.update(connection);
                this.needsUpdate = false;
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    public void updateDatabaseUserCount() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET users = ? WHERE id = ? LIMIT 1")) {
            statement.setInt(1, this.roomUnitManager.getRoomHabbosCount());
            statement.setInt(2, this.roomInfo.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    private void cycle() {
        this.cycleOdd = !this.cycleOdd;
        this.cycleTimestamp = System.currentTimeMillis();
        final boolean[] foundRightHolder = {false};
        
        boolean loaded;
        synchronized (this.loadLock) {
            loaded = this.loaded;
        }
        this.tileCache.clear();
        if (loaded) {
            if (!this.scheduledTasks.isEmpty()) {
                ConcurrentHashMap.KeySetView<Runnable, Boolean> tasks = this.scheduledTasks;
                this.scheduledTasks = ConcurrentHashMap.newKeySet();

                for (Runnable runnable : tasks) {
                    Emulator.getThreading().run(runnable);
                }
            }

            for (ICycleable task : this.roomSpecialTypes.getCycleTasks()) {
                task.cycle(this);
            }

            if (!this.roomUnitManager.getCurrentRoomHabbos().isEmpty()) {
                this.roomIdleCycles = 0;

                THashSet<RoomUnit> updatedUnit = new THashSet<>();
                ArrayList<Habbo> toKick = new ArrayList<>();

                final Room room = this;

                final long millis = System.currentTimeMillis();

                for (Habbo habbo : this.roomUnitManager.getCurrentRoomHabbos().values()) {
                    if (!foundRightHolder[0]) {
                        foundRightHolder[0] = habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE;
                    }

                    /* Habbo doesn't remove the handitem anymore, checked on February 25 2023
                    if (habbo.getRoomUnit().getHandItem() > 0 && millis - habbo.getRoomUnit().getHandItemTimestamp() > (Room.HAND_ITEM_TIME * 1000L)) {
                        this.giveHandItem(habbo, 0);
                    }

                     */

                    if (habbo.getRoomUnit().getEffectId() > 0 && millis / 1000 > habbo.getRoomUnit().getEffectEndTimestamp()) {
                        this.giveEffect(habbo, 0, -1);
                    }

                    if (habbo.getRoomUnit().isKicked()) {
                        habbo.getRoomUnit().setKickCount(habbo.getRoomUnit().getKickCount() + 1);

                        if (habbo.getRoomUnit().getKickCount() >= 5) {
                            this.scheduledTasks.add(() -> Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, room));
                            continue;
                        }
                    }

                    if (Emulator.getConfig().getBoolean("hotel.rooms.auto.idle")) {
                        if (!habbo.getRoomUnit().isIdle()) {
                            habbo.getRoomUnit().increaseIdleTimer();

                            if (habbo.getRoomUnit().isIdle()) {
                                boolean danceIsNone = (habbo.getRoomUnit().getDanceType() == DanceType.NONE);
                                if (danceIsNone)
                                    this.sendComposer(new SleepMessageComposer(habbo.getRoomUnit()).compose());
                                if (danceIsNone && !Emulator.getConfig().getBoolean("hotel.roomuser.idle.not_dancing.ignore.wired_idle"))
                                    WiredHandler.handle(WiredTriggerType.IDLES, habbo.getRoomUnit(), this, new Object[]{habbo});
                            }
                        } else {
                            habbo.getRoomUnit().increaseIdleTimer();

                            if (!this.getRoomInfo().isRoomOwner(habbo) && habbo.getRoomUnit().getIdleTicks() >= Room.IDLE_CYCLES_KICK) {
                                UserExitRoomEvent event = new UserExitRoomEvent(habbo, UserExitRoomEvent.UserExitRoomReason.KICKED_IDLE);
                                Emulator.getPluginManager().fireEvent(event);

                                if (!event.isCancelled()) {
                                    toKick.add(habbo);
                                }
                            }
                        }
                    }

                    if (Emulator.getConfig().getBoolean("hotel.rooms.deco_hosting") && this.roomInfo.getOwnerInfo().getId() != habbo.getHabboInfo().getId()) {
                        //Check if the time already have 1 minute (120 / 2 = 60s)
                        if (habbo.getRoomUnit().getTimeInRoom() >= 120) {
                            AchievementManager.progressAchievement(this.roomInfo.getOwnerInfo().getId(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHosting"));
                            habbo.getRoomUnit().resetTimeInRoom();
                        } else {
                            habbo.getRoomUnit().increaseTimeInRoom();
                        }
                    }

                    if (habbo.getHabboStats().isMutedBubbleTracker() && habbo.getHabboStats().allowTalk()) {
                        habbo.getHabboStats().setMutedBubbleTracker(false);
                        this.sendComposer(new IgnoreResultMessageComposer(habbo, IgnoreResultMessageComposer.UNIGNORED).compose());
                    }

                    // Substract 1 from the chatCounter every odd cycle, which is every (500ms * 2).
                    if (this.cycleOdd && habbo.getHabboStats().getChatCounter().get() > 0) {
                        habbo.getHabboStats().getChatCounter().decrementAndGet();
                    }

                    if (this.cycleRoomUnit(habbo.getRoomUnit())) {
                        updatedUnit.add(habbo.getRoomUnit());
                    }
                }

                if (!toKick.isEmpty()) {
                    for (Habbo habbo : toKick) {
                        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
                    }
                }

                if (!this.roomUnitManager.getCurrentRoomBots().isEmpty()) {
                    Iterator<Bot> botIterator = this.roomUnitManager.getCurrentRoomBots().values().iterator();

                    while(botIterator.hasNext()) {
                        try {
                            final Bot bot;
                            try {
                                bot = botIterator.next();
                            } catch (Exception e) {
                                break;
                            }

                            if (!this.allowBotsWalk && bot.getRoomUnit().isWalking()) {
                                bot.getRoomUnit().stopWalking();
                                updatedUnit.add(bot.getRoomUnit());
                                continue;
                            }

                            bot.cycle(this.allowBotsWalk);


                            if (this.cycleRoomUnit(bot.getRoomUnit())) {
                                updatedUnit.add(bot.getRoomUnit());
                            }


                        } catch (NoSuchElementException e) {
                            log.error(CAUGHT_EXCEPTION, e);
                            break;
                        }
                    }
                }

                if (!this.roomUnitManager.getCurrentRoomPets().isEmpty() && this.allowBotsWalk) {
                    Iterator<Pet> petIterator = this.roomUnitManager.getCurrentRoomPets().values().iterator();
                    while(petIterator.hasNext()) {
                        final Pet pet;
                        try {
                            pet = petIterator.next();
                        } catch (Exception e) {
                            break;
                        }

                        if (this.cycleRoomUnit(pet.getRoomUnit())) {
                            updatedUnit.add(pet.getRoomUnit());
                        }

                        pet.cycle();

                        if (pet.isPacketUpdate()) {
                            updatedUnit.add(pet.getRoomUnit());
                            pet.setPacketUpdate(false);
                        }

                        if (pet.getRoomUnit().isWalking() && pet.getRoomUnit().getPath().size() == 1 && pet.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE)) {
                            pet.getRoomUnit().removeStatus(RoomUnitStatus.GESTURE);
                            updatedUnit.add(pet.getRoomUnit());
                        }
                    }
                }


                if (this.roomInfo.getRollerSpeed() != -1 && this.rollerCycle >= this.roomInfo.getRollerSpeed()) {
                    this.rollerCycle = 0;

                    THashSet<MessageComposer> messages = new THashSet<>();

                    //Find alternative for this.
                    //Reason is that tile gets updated after every roller.
                    List<Integer> rollerFurniIds = new ArrayList<>();
                    List<Integer> rolledUnitIds = new ArrayList<>();


                    this.roomSpecialTypes.getRollers().forEachValue(roller -> {

                        RoomItem newRoller = null;

                        RoomTile rollerTile = this.getLayout().getTile(roller.getX(), roller.getY());

                        if (rollerTile == null)
                            return true;

                        THashSet<RoomItem> itemsOnRoller = new THashSet<>();

                        for (RoomItem item : getItemsAt(rollerTile)) {
                            if (item.getZ() >= roller.getZ() + Item.getCurrentHeight(roller)) {
                                itemsOnRoller.add(item);
                            }
                        }

                        itemsOnRoller.remove(roller);

                        if (!rollerTile.hasUnits() && itemsOnRoller.isEmpty())
                            return true;

                        RoomTile tileInFront = Room.this.layout.getTileInFront(Room.this.layout.getTile(roller.getX(), roller.getY()), roller.getRotation());

                        if (tileInFront == null)
                            return true;

                        if (!Room.this.layout.tileExists(tileInFront.getX(), tileInFront.getY()))
                            return true;

                        if (tileInFront.getState() == RoomTileState.INVALID)
                            return true;

                        if (!tileInFront.getAllowStack() && !(tileInFront.isWalkable() || tileInFront.getState() == RoomTileState.SIT || tileInFront.getState() == RoomTileState.LAY))
                            return true;

                        if (tileInFront.hasUnits())
                            return true;

                        THashSet<RoomItem> itemsNewTile = new THashSet<>();
                        itemsNewTile.addAll(getItemsAt(tileInFront));
                        itemsNewTile.removeAll(itemsOnRoller);

                        itemsOnRoller.removeIf(item -> item.getX() != roller.getX() || item.getY() != roller.getY() || rollerFurniIds.contains(item.getId()));

                        RoomItem topItem = Room.this.getTopItemAt(tileInFront.getX(), tileInFront.getY());

                        boolean allowUsers = true;
                        boolean allowFurniture = true;
                        boolean stackContainsRoller = false;

                        for (RoomItem item : itemsNewTile) {
                            if (!(item.getBaseItem().allowWalk() || item.getBaseItem().allowSit()) && !(item instanceof InteractionGate && item.getExtradata().equals("1"))) {
                                allowUsers = false;
                            }
                            if (item instanceof InteractionRoller) {
                                newRoller = item;
                                stackContainsRoller = true;

                                if ((item.getZ() != roller.getZ() || (itemsNewTile.size() > 1 && item != topItem)) && !InteractionRoller.NO_RULES) {
                                    allowUsers = false;
                                    allowFurniture = false;
                                    continue;
                                }

                                break;
                            } else {
                                allowFurniture = false;
                            }
                        }

                        if (allowFurniture) {
                            allowFurniture = tileInFront.getAllowStack();
                        }

                        double zOffset = 0;
                        if (newRoller != null) {
                            if ((!itemsNewTile.isEmpty() && (itemsNewTile.size() > 1)) && !InteractionRoller.NO_RULES) {
                                return true;
                            }
                        } else {
                            zOffset = -Item.getCurrentHeight(roller) + tileInFront.getStackHeight() - rollerTile.getZ();
                        }

                        if (allowUsers) {
                            Event roomUserRolledEvent = null;

                            if (Emulator.getPluginManager().isRegistered(UserRolledEvent.class, true)) {
                                roomUserRolledEvent = new UserRolledEvent(null, null, null);
                            }

                            ArrayList<RoomEntity> unitsOnTile = new ArrayList<>(rollerTile.getEntities());

                            for (RoomEntity entity : rollerTile.getEntities()) {
                                if(!(entity instanceof RoomUnit)) {
                                    continue;
                                }

                                RoomUnit unit = (RoomUnit) entity;

                                if (unit.getRoomUnitType() == RoomUnitType.PET) {
                                    Pet pet = this.roomUnitManager.getPetByRoomUnit(unit);
                                    if (pet instanceof RideablePet rideablePet && rideablePet.getRider() != null) {
                                        unitsOnTile.remove(unit);
                                    }
                                }
                            }

                            THashSet<Integer> usersRolledThisTile = new THashSet<>();

                            for (RoomEntity entity : unitsOnTile) {
                                if(!(entity instanceof RoomUnit)) {
                                    continue;
                                }

                                RoomUnit unit = (RoomUnit) entity;

                                if (rolledUnitIds.contains(unit.getVirtualId())) continue;

                                if (usersRolledThisTile.size() >= Room.ROLLERS_MAXIMUM_ROLL_AVATARS) break;

                                if (stackContainsRoller && !allowFurniture && !(topItem != null && topItem.isWalkable()))
                                    continue;

                                if (unit.hasStatus(RoomUnitStatus.MOVE))
                                    continue;

                                double newZ = unit.getCurrentZ() + zOffset;

                                if (roomUserRolledEvent != null && unit.getRoomUnitType() == RoomUnitType.HABBO) {
                                    roomUserRolledEvent = new UserRolledEvent(getHabbo(unit), roller, tileInFront);
                                    Emulator.getPluginManager().fireEvent(roomUserRolledEvent);

                                    if (roomUserRolledEvent.isCancelled())
                                        continue;
                                }

                                // horse riding
                                boolean isRiding = false;
                                if (unit.getRoomUnitType() == RoomUnitType.HABBO) {
                                    Habbo rollingHabbo = this.getHabbo(unit);
                                    if (rollingHabbo != null && rollingHabbo.getHabboInfo() != null) {
                                        RideablePet riding = rollingHabbo.getHabboInfo().getRiding();
                                        if (riding != null) {
                                            RoomUnit ridingUnit = riding.getRoomUnit();
                                            newZ = ridingUnit.getCurrentZ() + zOffset;
                                            rolledUnitIds.add(ridingUnit.getVirtualId());
                                            updatedUnit.remove(ridingUnit);
                                            messages.add(new RoomUnitOnRollerComposer(ridingUnit, roller, ridingUnit.getCurrentPosition(), ridingUnit.getCurrentZ(), tileInFront, newZ, room));
                                            isRiding = true;
                                        }
                                    }
                                }

                                usersRolledThisTile.add(unit.getVirtualId());
                                rolledUnitIds.add(unit.getVirtualId());
                                updatedUnit.remove(unit);
                                messages.add(new RoomUnitOnRollerComposer(unit, roller, unit.getCurrentPosition(), unit.getCurrentZ() + (isRiding ? 1 : 0), tileInFront, newZ + (isRiding ? 1 : 0), room));

                                if (itemsOnRoller.isEmpty()) {
                                    RoomItem item = room.getTopItemAt(tileInFront.getX(), tileInFront.getY());

                                    if (item != null && itemsNewTile.contains(item) && !itemsOnRoller.contains(item)) {
                                        Emulator.getThreading().run(() -> {
                                            if (unit.getGoalLocation() == rollerTile) {
                                                try {
                                                    item.onWalkOn(unit, room, new Object[]{rollerTile, tileInFront});
                                                } catch (Exception e) {
                                                    log.error(CAUGHT_EXCEPTION, e);
                                                }
                                            }
                                        }, this.roomInfo.getRollerSpeed() == 0 ? 250 : InteractionRoller.DELAY);
                                    }
                                }

                                if (unit.hasStatus(RoomUnitStatus.SIT)) {
                                    unit.setSitUpdate(true);
                                }
                            }
                        }

                        if (!messages.isEmpty()) {
                            for (MessageComposer message : messages) {
                                room.sendComposer(message.compose());
                            }
                            messages.clear();
                        }

                        if (allowFurniture || !stackContainsRoller || InteractionRoller.NO_RULES) {
                            Event furnitureRolledEvent = null;

                            if (Emulator.getPluginManager().isRegistered(FurnitureRolledEvent.class, true)) {
                                furnitureRolledEvent = new FurnitureRolledEvent(null, null, null);
                            }

                            if (newRoller == null || topItem == newRoller) {
                                List<RoomItem> sortedItems = new ArrayList<>(itemsOnRoller);
                                sortedItems.sort((o1, o2) -> Double.compare(o2.getZ(), o1.getZ()));

                                for (RoomItem item : sortedItems) {
                                    if ((item.getX() == roller.getX() && item.getY() == roller.getY() && zOffset <= 0) && (item != roller)) {
                                        if (furnitureRolledEvent != null) {
                                            furnitureRolledEvent = new FurnitureRolledEvent(item, roller, tileInFront);
                                            Emulator.getPluginManager().fireEvent(furnitureRolledEvent);

                                            if (furnitureRolledEvent.isCancelled())
                                                continue;
                                        }

                                        messages.add(new FloorItemOnRollerComposer(item, roller, tileInFront, zOffset, room));
                                        rollerFurniIds.add(item.getId());
                                    }
                                }
                            }
                        }


                        if (!messages.isEmpty()) {
                            for (MessageComposer message : messages) {
                                room.sendComposer(message.compose());
                            }
                            messages.clear();
                        }

                        return true;
                    });


                    int currentTime = (int) (this.cycleTimestamp / 1000);
                    for (RoomItem pyramid : this.roomSpecialTypes.getItemsOfType(InteractionPyramid.class)) {
                        if (pyramid instanceof InteractionPyramid interactionPyramid && interactionPyramid.getNextChange() < currentTime) {
                            interactionPyramid.change(this);
                        }
                    }
                } else {
                    this.rollerCycle++;
                }

                if (!updatedUnit.isEmpty()) {
                    this.sendComposer(new UserUpdateComposer(updatedUnit).compose());
                }

                this.traxManager.cycle();
            } else {

                if (this.roomIdleCycles < 60)
                    this.roomIdleCycles++;
                else
                    this.dispose();
            }
        }

        synchronized (this.habboQueue) {
            if (!this.habboQueue.isEmpty() && !foundRightHolder[0]) {
                this.habboQueue.forEachEntry((a, b) -> {
                    if (b.isOnline()) {
                        if (b.getHabboInfo().getRoomQueueId() == this.roomInfo.getId()) {
                            b.getClient().sendResponse(new FlatAccessDeniedMessageComposer(""));
                        }
                    }
                    return true;
                });

                this.habboQueue.clear();
            }
        }

        if (!this.scheduledComposers.isEmpty()) {
            for (ServerMessage message : this.scheduledComposers) {
                this.sendComposer(message);
            }

            this.scheduledComposers.clear();
        }
    }

    private boolean cycleRoomUnit(RoomUnit unit) {
        boolean update = unit.isStatusUpdateNeeded();

        if (unit.hasStatus(RoomUnitStatus.SIGN)) {
            this.sendComposer(new UserUpdateComposer(unit).compose());
            unit.removeStatus(RoomUnitStatus.SIGN);
        }

        if (unit.isWalking() && unit.getPath() != null && !unit.getPath().isEmpty()) {
            if (!unit.cycle(this)) {
                return true;
            }
        } else {
            if (unit.hasStatus(RoomUnitStatus.MOVE) && !unit.isAnimateWalk()) {
                unit.removeStatus(RoomUnitStatus.MOVE);

                update = true;
            }

            if (!unit.isWalking() && !unit.isCmdSitEnabled()) {
                RoomTile thisTile = this.getLayout().getTile(unit.getCurrentPosition().getX(), unit.getCurrentPosition().getY());
                RoomItem topItem = this.getTallestChair(thisTile);

                if (topItem == null || !topItem.getBaseItem().allowSit()) {
                    if (unit.hasStatus(RoomUnitStatus.SIT)) {
                        unit.removeStatus(RoomUnitStatus.SIT);
                        update = true;
                    }
                } else if (thisTile.getState() == RoomTileState.SIT && (!unit.hasStatus(RoomUnitStatus.SIT) || unit.isSitUpdate())) {
                    if(unit instanceof RoomAvatar roomAvatar) {
                        roomAvatar.setDance(DanceType.NONE);
                    }
                    unit.setStatus(RoomUnitStatus.SIT, (Item.getCurrentHeight(topItem)) + "");
                    unit.setCurrentZ(topItem.getZ());
                    unit.setRotation(RoomRotation.values()[topItem.getRotation()]);
                    unit.setSitUpdate(false);
                    return true;
                }
            }
        }

        if (!unit.isWalking() && !unit.isCmdLayEnabled()) {
            RoomItem topItem = this.getTopItemAt(unit.getCurrentPosition().getX(), unit.getCurrentPosition().getY());

            if (topItem == null || !topItem.getBaseItem().allowLay()) {
                if (unit.hasStatus(RoomUnitStatus.LAY)) {
                    unit.removeStatus(RoomUnitStatus.LAY);
                    update = true;
                }
            } else {
                if (!unit.hasStatus(RoomUnitStatus.LAY)) {
                    unit.setStatus(RoomUnitStatus.LAY, Item.getCurrentHeight(topItem) + "");
                    unit.setRotation(RoomRotation.values()[topItem.getRotation() % 4]);

                    if (topItem.getRotation() == 0 || topItem.getRotation() == 4) {
                        unit.setLocation(this.layout.getTile(unit.getCurrentPosition().getX(), topItem.getY()));
                    } else {
                        unit.setLocation(this.layout.getTile(topItem.getX(), unit.getCurrentPosition().getY()));
                    }
                    update = true;
                }
            }
        }

        if (update) {
            unit.setStatusUpdateNeeded(false);
        }

        return update;
    }

    public void setDiagonalMoveEnabled(boolean moveDiagonally) {
        this.roomInfo.setDiagonalMoveEnabled(moveDiagonally);
        this.layout.moveDiagonally(moveDiagonally);
        this.needsUpdate = true;
    }

    public Color getBackgroundTonerColor() {
        Color color = new Color(0, 0, 0);
        TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i > 0; i--) {
            try {
                iterator.advance();
                RoomItem object = iterator.value();

                if (object instanceof InteractionBackgroundToner) {
                    String[] extraData = object.getExtradata().split(":");

                    if (extraData.length == 4 && extraData[0].equalsIgnoreCase("1")) {
                        return Color.getHSBColor(Integer.parseInt(extraData[1]), Integer.parseInt(extraData[2]), Integer.parseInt(extraData[3]));

                    }
                }
            } catch (Exception ignored) {
            }
        }

        return color;
    }

    public void setRollerSpeed(int rollerSpeed) {
        this.roomInfo.setRollerSpeed(rollerSpeed);
        this.rollerCycle = 0;
        this.needsUpdate = true;
    }

    public String[] filterAnything() {
        return new String[]{this.roomInfo.getOwnerInfo().getUsername(), this.roomInfo.getGuildName(), this.roomInfo.getDescription(), this.getPromotionDesc()};
    }

    public boolean isPromoted() {
        this.roomInfo.setPromoted(this.promotion != null && this.promotion.getEndTimestamp() > Emulator.getIntUnixTimestamp());
        this.needsUpdate = true;
        return this.roomInfo.isPromoted();
    }

    public String getPromotionDesc() {
        if (this.promotion != null) {
            return this.promotion.getDescription();
        }

        return "";
    }

    public void createPromotion(String title, String description, int category) {
        this.roomInfo.setPromoted(true);

        if (this.promotion == null) {
            this.promotion = new RoomPromotion(this, title, description, Emulator.getIntUnixTimestamp() + (120 * 60), Emulator.getIntUnixTimestamp(), category);
        } else {
            this.promotion.setTitle(title);
            this.promotion.setDescription(description);
            this.promotion.setEndTimestamp(Emulator.getIntUnixTimestamp() + (120 * 60));
            this.promotion.setCategory(category);
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_promotions (room_id, title, description, end_timestamp, start_timestamp, category) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = ?, description = ?, end_timestamp = ?, category = ?")) {
            statement.setInt(1, this.roomInfo.getId());
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setInt(4, this.promotion.getEndTimestamp());
            statement.setInt(5, this.promotion.getStartTimestamp());
            statement.setInt(6, category);
            statement.setString(7, this.promotion.getTitle());
            statement.setString(8, this.promotion.getDescription());
            statement.setInt(9, this.promotion.getEndTimestamp());
            statement.setInt(10, this.promotion.getCategory());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        this.needsUpdate = true;
    }

    public boolean addGame(Game game) {
        synchronized (this.games) {
            return this.games.add(game);
        }
    }

    public boolean deleteGame(Game game) {
        game.stop();
        game.dispose();
        synchronized (this.games) {
            return this.games.remove(game);
        }
    }

    public Game getGame(Class<? extends Game> gameType) {
        if (gameType == null) return null;

        synchronized (this.games) {
            for (Game game : this.games) {
                if (gameType.isInstance(game)) {
                    return game;
                }
            }
        }

        return null;
    }

    public Game getGameOrCreate(Class<? extends Game> gameType) {
        Game game = this.getGame(gameType);
        if (game == null) {
            try {
                game = gameType.getDeclaredConstructor(Room.class).newInstance(this);
                this.addGame(game);
            } catch (Exception e) {
                log.error("Error getting game " + gameType.getName(), e);
            }
        }

        return game;
    }

    public String getFurniOwnerName(int userId) {
        return this.furniOwnerNames.get(userId);
    }

    public void addToQueue(Habbo habbo) {
        synchronized (this.habboQueue) {
            this.habboQueue.put(habbo.getHabboInfo().getId(), habbo);
        }
    }

    public boolean removeFromQueue(Habbo habbo) {
        try {
            this.sendComposer(new FlatAccessibleMessageComposer(habbo.getHabboInfo().getUsername()).compose());

            synchronized (this.habboQueue) {
                return this.habboQueue.remove(habbo.getHabboInfo().getId()) != null;
            }
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }

        return true;
    }

    public void addHabboItem(RoomItem item) {
        if (item == null)
            return;

        synchronized (this.roomItems) {
            try {
                this.roomItems.put(item.getId(), item);
            } catch (Exception ignored) {

            }
        }

        synchronized (this.furniOwnerCount) {
            this.furniOwnerCount.put(item.getUserId(), this.furniOwnerCount.get(item.getUserId()) + 1);
        }

        synchronized (this.furniOwnerNames) {
            if (!this.furniOwnerNames.containsKey(item.getUserId())) {
                HabboInfo habbo = HabboManager.getOfflineHabboInfo(item.getUserId());

                if (habbo != null) {
                    this.furniOwnerNames.put(item.getUserId(), habbo.getUsername());
                } else {
                    log.error("Failed to find username for item (ID: {}, UserID: {})", item.getId(), item.getUserId());
                }
            }
        }

        //TODO: Move this list
        synchronized (this.roomSpecialTypes) {
            if (item instanceof ICycleable) {
                this.roomSpecialTypes.addCycleTask((ICycleable) item);
            }

            if (item instanceof InteractionWiredTrigger interactionWiredTrigger) {
                this.roomSpecialTypes.addTrigger(interactionWiredTrigger);
            } else if (item instanceof InteractionWiredEffect interactionWiredEffect) {
                this.roomSpecialTypes.addEffect(interactionWiredEffect);
            } else if (item instanceof InteractionWiredCondition interactionWiredCondition) {
                this.roomSpecialTypes.addCondition(interactionWiredCondition);
            } else if (item instanceof InteractionWiredExtra interactionWiredExtra) {
                this.roomSpecialTypes.addExtra(interactionWiredExtra);
            } else if (item instanceof InteractionBattleBanzaiTeleporter interactionBattleBanzaiTeleporter) {
                this.roomSpecialTypes.addBanzaiTeleporter(interactionBattleBanzaiTeleporter);
            } else if (item instanceof InteractionRoller interactionRoller) {
                this.roomSpecialTypes.addRoller(interactionRoller);
            } else if (item instanceof InteractionGameScoreboard interactionGameScoreboard) {
                this.roomSpecialTypes.addGameScoreboard(interactionGameScoreboard);
            } else if (item instanceof InteractionGameGate interactionGameGate) {
                this.roomSpecialTypes.addGameGate(interactionGameGate);
            } else if (item instanceof InteractionGameTimer interactionGameTimer) {
                this.roomSpecialTypes.addGameTimer(interactionGameTimer);
            } else if (item instanceof InteractionFreezeExitTile interactionFreezeExitTile) {
                this.roomSpecialTypes.addFreezeExitTile(interactionFreezeExitTile);
            } else if (item instanceof InteractionNest interactionNest) {
                this.roomSpecialTypes.addNest(interactionNest);
            } else if (item instanceof InteractionPetDrink interactionPetDrink) {
                this.roomSpecialTypes.addPetDrink(interactionPetDrink);
            } else if (item instanceof InteractionPetFood interactionPetFood) {
                this.roomSpecialTypes.addPetFood(interactionPetFood);
            } else if (item instanceof InteractionPetToy interactionPetToy) {
                this.roomSpecialTypes.addPetToy(interactionPetToy);
            } else if (item instanceof InteractionPetTree) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPetTrampoline) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMoodLight) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPyramid) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMusicDisc) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBattleBanzaiSphere) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTalkingFurniture) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWater) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWaterItem) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMuteArea) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBuildArea) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagPole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagField) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionJukeBox) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPetBreedingNest) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBlackHole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWiredHighscore) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionStickyPole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof WiredBlob) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTent) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionSnowboardSlope) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionFireworks) {
                this.roomSpecialTypes.addUndefined(item);
            }

        }
    }

    public RoomItem getHabboItem(int id) {
        if (this.roomItems == null || this.roomSpecialTypes == null)
            return null; // room not loaded completely

        RoomItem item;
        synchronized (this.roomItems) {
            item = this.roomItems.get(id);
        }

        if (item == null)
            item = this.roomSpecialTypes.getBanzaiTeleporter(id);

        if (item == null)
            item = this.roomSpecialTypes.getTrigger(id);

        if (item == null)
            item = this.roomSpecialTypes.getEffect(id);

        if (item == null)
            item = this.roomSpecialTypes.getCondition(id);

        if (item == null)
            item = this.roomSpecialTypes.getGameGate(id);

        if (item == null)
            item = this.roomSpecialTypes.getGameScorebord(id);

        if (item == null)
            item = this.roomSpecialTypes.getGameTimer(id);

        if (item == null)
            item = this.roomSpecialTypes.getFreezeExitTiles().get(id);

        if (item == null)
            item = this.roomSpecialTypes.getRoller(id);

        if (item == null)
            item = this.roomSpecialTypes.getNest(id);

        if (item == null)
            item = this.roomSpecialTypes.getPetDrink(id);

        if (item == null)
            item = this.roomSpecialTypes.getPetFood(id);

        return item;
    }

    void removeHabboItem(int id) {
        this.removeHabboItem(this.getHabboItem(id));
    }

    public void removeHabboItem(RoomItem item) {
        if (item != null) {

            RoomItem i;
            synchronized (this.roomItems) {
                i = this.roomItems.remove(item.getId());
            }

            if (i != null) {
                synchronized (this.furniOwnerCount) {
                    synchronized (this.furniOwnerNames) {
                        int count = this.furniOwnerCount.get(i.getUserId());

                        if (count > 1)
                            this.furniOwnerCount.put(i.getUserId(), count - 1);
                        else {
                            this.furniOwnerCount.remove(i.getUserId());
                            this.furniOwnerNames.remove(i.getUserId());
                        }
                    }
                }

                if (item instanceof ICycleable) {
                    this.roomSpecialTypes.removeCycleTask((ICycleable) item);
                }

                if (item instanceof InteractionBattleBanzaiTeleporter) {
                    this.roomSpecialTypes.removeBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
                } else if (item instanceof InteractionWiredTrigger) {
                    this.roomSpecialTypes.removeTrigger((InteractionWiredTrigger) item);
                } else if (item instanceof InteractionWiredEffect) {
                    this.roomSpecialTypes.removeEffect((InteractionWiredEffect) item);
                } else if (item instanceof InteractionWiredCondition) {
                    this.roomSpecialTypes.removeCondition((InteractionWiredCondition) item);
                } else if (item instanceof InteractionWiredExtra) {
                    this.roomSpecialTypes.removeExtra((InteractionWiredExtra) item);
                } else if (item instanceof InteractionRoller) {
                    this.roomSpecialTypes.removeRoller((InteractionRoller) item);
                } else if (item instanceof InteractionGameScoreboard) {
                    this.roomSpecialTypes.removeScoreboard((InteractionGameScoreboard) item);
                } else if (item instanceof InteractionGameGate) {
                    this.roomSpecialTypes.removeGameGate((InteractionGameGate) item);
                } else if (item instanceof InteractionGameTimer) {
                    this.roomSpecialTypes.removeGameTimer((InteractionGameTimer) item);
                } else if (item instanceof InteractionFreezeExitTile) {
                    this.roomSpecialTypes.removeFreezeExitTile((InteractionFreezeExitTile) item);
                } else if (item instanceof InteractionNest) {
                    this.roomSpecialTypes.removeNest((InteractionNest) item);
                } else if (item instanceof InteractionPetDrink) {
                    this.roomSpecialTypes.removePetDrink((InteractionPetDrink) item);
                } else if (item instanceof InteractionPetFood) {
                    this.roomSpecialTypes.removePetFood((InteractionPetFood) item);
                } else if (item instanceof InteractionPetToy) {
                    this.roomSpecialTypes.removePetToy((InteractionPetToy) item);
                } else if (item instanceof InteractionPetTree) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionPetTrampoline) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionMoodLight) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionPyramid) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionMusicDisc) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionBattleBanzaiSphere) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTalkingFurniture) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWaterItem) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWater) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionMuteArea) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTagPole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTagField) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionJukeBox) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionPetBreedingNest) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionBlackHole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWiredHighscore) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionStickyPole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof WiredBlob) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTent) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionSnowboardSlope) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionBuildArea) {
                    this.roomSpecialTypes.removeUndefined(item);
                }
            }
        }
    }

    public List<RoomItem> getFloorItems() {
        return roomItems.valueCollection().stream().filter(i -> i.getBaseItem().getType() == FurnitureType.FLOOR).toList();
    }

    public List<RoomItem> getWallItems() {
        return roomItems.valueCollection().stream().filter(i -> i.getBaseItem().getType() == FurnitureType.WALL).toList();
    }

    public List<RoomItem> getPostItNotes() {
        return roomItems.valueCollection().stream().filter(i -> i.getBaseItem().getInteractionType().getType() == InteractionPostIt.class).toList();
    }

    public void kickHabbo(Habbo habbo, boolean alert) {
        if (alert) {
            habbo.getClient().sendResponse(new GenericErrorComposer(GenericErrorComposer.KICKED_OUT_OF_THE_ROOM));
        }

        habbo.getRoomUnit().setKicked(true);
        habbo.getRoomUnit().setGoalLocation(this.layout.getDoorTile());

        if (habbo.getRoomUnit().getPath() == null || habbo.getRoomUnit().getPath().size() <= 1 || this.roomInfo.isPublicRoom()) {
            habbo.getRoomUnit().setCanWalk(true);
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
        }
    }

    public THashSet<Habbo> getHabbosOnItem(RoomItem item) {
        THashSet<Habbo> habbos = new THashSet<>();
        for (short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x++) {
            for (short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y++) {
                RoomTile tile = this.getLayout().getTile(x, y);
                habbos.addAll(this.roomUnitManager.getHabbosAt(tile));
            }
        }

        return habbos;
    }

    public THashSet<Bot> getBotsOnItem(RoomItem item) {
        THashSet<Bot> bots = new THashSet<>();
        for (short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x++) {
            for (short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y++) {
                RoomTile tile = this.getLayout().getTile(x, y);
                bots.addAll(this.roomUnitManager.getBotsAt(tile));
            }
        }

        return bots;
    }

    public THashSet<Pet> getPetsOnItem(RoomItem item) {
        THashSet<Pet> pets = new THashSet<>();
        for (short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x++) {
            for (short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y++) {
                RoomTile tile = this.getLayout().getTile(x, y);
                pets.addAll(this.roomUnitManager.getPetsAt(tile));
            }
        }

        return pets;
    }

    public void teleportHabboToItem(Habbo habbo, RoomItem item) {
        this.teleportRoomUnitToLocation(habbo.getRoomUnit(), item.getX(), item.getY(), item.getZ() + Item.getCurrentHeight(item));
    }

    public void teleportHabboToLocation(Habbo habbo, short x, short y) {
        this.teleportRoomUnitToLocation(habbo.getRoomUnit(), x, y, 0.0);
    }

    public void teleportRoomUnitToItem(RoomUnit roomUnit, RoomItem item) {
        this.teleportRoomUnitToLocation(roomUnit, item.getX(), item.getY(), item.getZ() + Item.getCurrentHeight(item));
    }

    public void teleportRoomUnitToLocation(RoomUnit roomUnit, short x, short y) {
        this.teleportRoomUnitToLocation(roomUnit, x, y, 0.0);
    }

    public void teleportRoomUnitToLocation(RoomUnit roomUnit, short x, short y, double z) {
        if (this.loaded) {
            RoomTile tile = this.layout.getTile(x, y);

            if (z < tile.getZ()) {
                z = tile.getZ();
            }

            roomUnit.setLocation(tile);
            roomUnit.setGoalLocation(tile);
            roomUnit.setCurrentZ(z);
            roomUnit.setPreviousLocationZ(z);
            this.updateRoomUnit(roomUnit);


        }
    }

    public void muteHabbo(Habbo habbo, int minutes) {
        synchronized (this.mutedHabbos) {
            this.mutedHabbos.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + (minutes * 60));
        }
    }

    public boolean isMuted(Habbo habbo) {
        if (this.getRoomInfo().isRoomOwner(habbo) || this.hasRights(habbo))
            return false;

        if (this.mutedHabbos.containsKey(habbo.getHabboInfo().getId())) {
            boolean time = this.mutedHabbos.get(habbo.getHabboInfo().getId()) > Emulator.getIntUnixTimestamp();

            if (!time) {
                this.mutedHabbos.remove(habbo.getHabboInfo().getId());
            }

            return time;
        }

        return false;
    }

    public void habboEntered(Habbo habbo) {
        habbo.getRoomUnit().setAnimateWalk(false);

        synchronized (this.roomUnitManager.getCurrentRoomBots()) {
            if (habbo.getHabboInfo().getId() != this.roomInfo.getOwnerInfo().getId())
                return;

            Iterator<Bot> botIterator = this.roomUnitManager.getCurrentRoomBots().values().iterator();

            while (botIterator.hasNext()) {
                try {
                    Bot bot = botIterator.next();

                    if (bot instanceof VisitorBot visitorBot) {
                        visitorBot.onUserEnter(habbo);
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }

        RoomItem doorTileTopItem = this.getTopItemAt(habbo.getRoomUnit().getCurrentPosition().getX(), habbo.getRoomUnit().getCurrentPosition().getY());
        if (doorTileTopItem != null && !(doorTileTopItem instanceof InteractionTeleportTile)) {
            try {
                doorTileTopItem.onWalkOn(habbo.getRoomUnit(), this, new Object[]{});
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION, e);
            }
        }
    }

    public void floodMuteHabbo(Habbo habbo, int timeOut) {
        habbo.getHabboStats().setMutedCount(habbo.getHabboStats().getMutedCount() + 1);
        timeOut += (timeOut * (int) Math.ceil(Math.pow(habbo.getHabboStats().getMutedCount(), 2)));
        habbo.getHabboStats().getChatCounter().set(0);
        habbo.mute(timeOut, true);
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType) {
        this.talk(habbo, roomChatMessage, chatType, false);
    }

    public void talk(final Habbo habbo, final RoomChatMessage roomChatMessage, RoomChatType chatType, boolean ignoreWired) {
        if (!habbo.getHabboStats().allowTalk())
            return;

        if (habbo.getRoomUnit().isInvisible() && Emulator.getConfig().getBoolean("invisible.prevent.chat", false)) {
            if (!Emulator.getGameEnvironment().getCommandsManager().handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                habbo.whisper(Emulator.getTexts().getValue("invisible.prevent.chat.error"));
            }

            return;
        }

        if (habbo.getRoomUnit().getRoom() != this)
            return;

        long millis = System.currentTimeMillis();
        if (HABBO_CHAT_DELAY && millis - habbo.getHabboStats().getLastChat() < 750) {
            return;
        }

        habbo.getHabboStats().setLastChat(millis);
        if (roomChatMessage != null && Emulator.getConfig().getBoolean("easter_eggs.enabled") && roomChatMessage.getMessage().equalsIgnoreCase("i am a pirate")) {
            habbo.getHabboStats().getChatCounter().addAndGet(1);
            Emulator.getThreading().run(new YouAreAPirate(habbo, this));
            return;
        }

        UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.TALKED, false);
        Emulator.getPluginManager().fireEvent(event);

        if (!event.isCancelled() && !event.isIdle()) {
            this.unIdle(habbo);
        }

        this.sendComposer(new UserTypingMessageComposer(habbo.getRoomUnit(), false).compose());

        if (roomChatMessage == null || roomChatMessage.getMessage() == null || roomChatMessage.getMessage().equals(""))
            return;

        if (!habbo.hasRight(Permission.ACC_NOMUTE) && (!MUTEAREA_CAN_WHISPER || chatType != RoomChatType.WHISPER)) {
            for (RoomItem area : this.getRoomSpecialTypes().getItemsOfType(InteractionMuteArea.class)) {
                if (((InteractionMuteArea) area).inSquare(habbo.getRoomUnit().getCurrentPosition())) {
                    return;
                }
            }
        }

        if (!this.wordFilterWords.isEmpty() && !habbo.hasRight(Permission.ACC_CHAT_NO_FILTER)) {
            for (String string : this.wordFilterWords) {
                roomChatMessage.setMessage(roomChatMessage.getMessage().replaceAll("(?i)" + Pattern.quote(string), "bobba"));
            }
        }

        if (!habbo.hasRight(Permission.ACC_NOMUTE)) {
            if (this.isMuted() && !this.hasRights(habbo)) {
                return;
            }

            if (this.isMuted(habbo)) {
                habbo.getClient().sendResponse(new RemainingMutePeriodComposer(this.mutedHabbos.get(habbo.getHabboInfo().getId()) - Emulator.getIntUnixTimestamp()));
                return;
            }
        }

        if (chatType != RoomChatType.WHISPER) {
            if (Emulator.getGameEnvironment().getCommandsManager().handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                WiredHandler.handle(WiredTriggerType.SAY_COMMAND, habbo.getRoomUnit(), habbo.getRoomUnit().getRoom(), new Object[]{roomChatMessage.getMessage()});
                roomChatMessage.isCommand = true;
                return;
            }

            if (!ignoreWired) {
                if (WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, habbo.getRoomUnit(), habbo.getRoomUnit().getRoom(), new Object[]{roomChatMessage.getMessage()})) {
                    habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(roomChatMessage.getMessage(), habbo, habbo, roomChatMessage.getBubble())));
                    return;
                }
            }
        }

        if (!habbo.hasRight(Permission.ACC_CHAT_NO_FLOOD)) {
            final int chatCounter = habbo.getHabboStats().getChatCounter().addAndGet(1);

            if (chatCounter > 3) {
                final boolean floodRights = Emulator.getConfig().getBoolean("flood.with.rights");
                final boolean hasRights = this.hasRights(habbo);

                if (floodRights || !hasRights) {
                    if (this.roomInfo.getChatProtection() == 0 || (this.roomInfo.getChatProtection() == 1 && chatCounter > 4) || (this.roomInfo.getChatProtection() == 2 && chatCounter > 5)) {
                        this.floodMuteHabbo(habbo, muteTime);
                        return;
                    }
                }
            }
        }

        ServerMessage prefixMessage = null;

        if (Emulator.getPluginManager().isRegistered(UsernameTalkEvent.class, true)) {
            UsernameTalkEvent usernameTalkEvent = Emulator.getPluginManager().fireEvent(new UsernameTalkEvent(habbo, roomChatMessage, chatType));
            if (usernameTalkEvent.hasCustomComposer()) {
                prefixMessage = usernameTalkEvent.getCustomComposer();
            }
        }

        if (prefixMessage == null) {
            prefixMessage = roomChatMessage.getHabbo().getHabboInfo().getPermissionGroup().hasPrefix() ? new UserNameChangedMessageComposer(habbo, true).compose() : null;
        }
        ServerMessage clearPrefixMessage = prefixMessage != null ? new UserNameChangedMessageComposer(habbo).compose() : null;

        Rectangle tentRectangle = this.roomSpecialTypes.tentAt(habbo.getRoomUnit().getCurrentPosition());

        String trimmedMessage = roomChatMessage.getMessage().replaceAll("\\s+$", "");

        if (trimmedMessage.isEmpty()) trimmedMessage = " ";

        roomChatMessage.setMessage(trimmedMessage);

        if (chatType == RoomChatType.WHISPER) {
            if (roomChatMessage.getTargetHabbo() == null) {
                return;
            }

            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("To " + staffChatMessage.getTargetHabbo().getHabboInfo().getUsername() + ": " + staffChatMessage.getMessage());

            final ServerMessage message = new WhisperMessageComposer(roomChatMessage).compose();
            final ServerMessage staffMessage = new WhisperMessageComposer(staffChatMessage).compose();

            for (Habbo h : this.roomUnitManager.getRoomHabbos()) {
                if (h == roomChatMessage.getTargetHabbo() || h == habbo) {
                    if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) {
                        if (prefixMessage != null) {
                            h.getClient().sendResponse(prefixMessage);
                        }
                        h.getClient().sendResponse(message);

                        if (clearPrefixMessage != null) {
                            h.getClient().sendResponse(clearPrefixMessage);
                        }
                    }

                    continue;
                }
                if (h.hasRight(Permission.ACC_SEE_WHISPERS)) {
                    h.getClient().sendResponse(staffMessage);
                }
            }
        } else if (chatType == RoomChatType.TALK) {
            ServerMessage message = new ChatMessageComposer(roomChatMessage).compose();
            boolean noChatLimit = habbo.hasRight(Permission.ACC_CHAT_NO_LIMIT);

            for (Habbo h : this.roomUnitManager.getRoomHabbos()) {
                if ((h.getRoomUnit().getCurrentPosition().distance(habbo.getRoomUnit().getCurrentPosition()) <= this.roomInfo.getChatDistance() ||
                        h.equals(habbo) ||
                        this.hasRights(h) ||
                        noChatLimit) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentPosition()))) {
                    if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) {
                        if (prefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                            h.getClient().sendResponse(prefixMessage);
                        }
                        h.getClient().sendResponse(message);
                        if (clearPrefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                            h.getClient().sendResponse(clearPrefixMessage);
                        }
                    }
                    continue;
                }
                // Staff should be able to see the tent chat anyhow
                showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
            }
        } else if (chatType == RoomChatType.SHOUT) {
            ServerMessage message = new ShoutMessageComposer(roomChatMessage).compose();

            for (Habbo h : this.roomUnitManager.getRoomHabbos()) {
                // Show the message
                // If the receiving Habbo has not ignored the sending Habbo
                // AND the sending Habbo is NOT in a tent OR the receiving Habbo is in the same tent as the sending Habbo
                if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId()) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentPosition()))) {
                    if (prefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse(message);
                    if (clearPrefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                        h.getClient().sendResponse(clearPrefixMessage);
                    }
                    continue;
                }
                // Staff should be able to see the tent chat anyhow, even when not in the same tent
                showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
            }
        }

        if (chatType == RoomChatType.TALK || chatType == RoomChatType.SHOUT) {
            synchronized (this.roomUnitManager.getCurrentRoomBots()) {
                Iterator<Bot> botIterator = this.roomUnitManager.getCurrentRoomBots().values().iterator();

                while (botIterator.hasNext()) {
                    try {
                        Bot bot = botIterator.next();
                        bot.onUserSay(roomChatMessage);

                    } catch (NoSuchElementException e) {
                        log.error(CAUGHT_EXCEPTION, e);
                        break;
                    }
                }
            }

            if (roomChatMessage.getBubble().triggersTalkingFurniture()) {
                THashSet<RoomItem> items = this.roomSpecialTypes.getItemsOfType(InteractionTalkingFurniture.class);

                for (RoomItem item : items) {
                    if (this.layout.getTile(item.getX(), item.getY()).distance(habbo.getRoomUnit().getCurrentPosition()) <= Emulator.getConfig().getInt("furniture.talking.range")) {
                        int count = Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.count", 0);

                        if (count > 0) {
                            int randomValue = Emulator.getRandom().nextInt(count + 1);

                            RoomChatMessage itemMessage = new RoomChatMessage(Emulator.getTexts().getValue(item.getBaseItem().getName() + ".message." + randomValue, item.getBaseItem().getName() + ".message." + randomValue + " not found!"), habbo, RoomChatMessageBubbles.getBubble(Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.bubble", RoomChatMessageBubbles.PARROT.getType())));

                            this.sendComposer(new ChatMessageComposer(itemMessage).compose());

                            try {
                                item.onClick(habbo.getClient(), this, new Object[0]);
                                item.setExtradata("1");
                                updateItemState(item);

                                Emulator.getThreading().run(() -> {
                                    item.setExtradata("0");
                                    updateItemState(item);
                                }, 2000);

                                break;
                            } catch (Exception e) {
                                log.error(CAUGHT_EXCEPTION, e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends the given message to the receiving Habbo if the Habbo has the ACC_SEE_TENTCHAT permission and is not within the tent
     *
     * @param receivingHabbo  The receiving Habbo
     * @param roomChatMessage The message to receive
     * @param tentRectangle   The whole tent area from where the sending Habbo is saying something
     */
    private void showTentChatMessageOutsideTentIfPermitted(Habbo receivingHabbo, RoomChatMessage roomChatMessage, Rectangle tentRectangle) {
        if (receivingHabbo != null && receivingHabbo.hasRight(Permission.ACC_SEE_TENTCHAT) && tentRectangle != null && !RoomLayout.tileInSquare(tentRectangle, receivingHabbo.getRoomUnit().getCurrentPosition())) {
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("[" + Emulator.getTexts().getValue("hotel.room.tent.prefix") + "] " + staffChatMessage.getMessage());
            final ServerMessage staffMessage = new WhisperMessageComposer(staffChatMessage).compose();
            receivingHabbo.getClient().sendResponse(staffMessage);
        }
    }

    public THashSet<RoomTile> getLockedTiles() {
        THashSet<RoomTile> lockedTiles = new THashSet<>();

        TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();


        for (int i = this.roomItems.size(); i-- > 0; ) {
            RoomItem item;
            try {
                iterator.advance();
                item = iterator.value();
            } catch (Exception e) {
                break;
            }

            if (item.getBaseItem().getType() != FurnitureType.FLOOR)
                continue;

            boolean found = lockedTiles.stream().anyMatch(tile -> tile.getX() == item.getX() && tile.getY() == item.getY());

            if (!found) {
                if (item.getRotation() == 0 || item.getRotation() == 4) {
                    for (short y = 0; y < item.getBaseItem().getLength(); y++) {
                        for (short x = 0; x < item.getBaseItem().getWidth(); x++) {
                            RoomTile tile = this.layout.getTile((short) (item.getX() + x), (short) (item.getY() + y));

                            if (tile != null) {
                                lockedTiles.add(tile);
                            }
                        }
                    }
                } else {
                    for (short y = 0; y < item.getBaseItem().getWidth(); y++) {
                        for (short x = 0; x < item.getBaseItem().getLength(); x++) {
                            RoomTile tile = this.layout.getTile((short) (item.getX() + x), (short) (item.getY() + y));

                            if (tile != null) {
                                lockedTiles.add(tile);
                            }
                        }
                    }
                }
            }
        }

        return lockedTiles;
    }

    @Deprecated
    public THashSet<RoomItem> getItemsAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short) x, (short) y);

        if (tile != null) {
            return this.getItemsAt(tile);
        }

        return new THashSet<>(0);
    }

    public THashSet<RoomItem> getItemsAt(RoomTile tile) {
        return getItemsAt(tile, false);
    }

    public THashSet<RoomItem> getItemsAt(RoomTile tile, boolean returnOnFirst) {
        THashSet<RoomItem> items = new THashSet<>(0);

        if (tile == null)
            return items;

        if (this.loaded) {
            THashSet<RoomItem> cachedItems = this.tileCache.get(tile);
            if (cachedItems != null)
                return cachedItems;
        }

        TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; ) {
            RoomItem item;
            try {
                iterator.advance();
                item = iterator.value();
            } catch (Exception e) {
                break;
            }

            if (item == null)
                continue;

            if (item.getBaseItem().getType() != FurnitureType.FLOOR)
                continue;

            int width, length;

            if (item.getRotation() != 2 && item.getRotation() != 6) {
                width = item.getBaseItem().getWidth() > 0 ? item.getBaseItem().getWidth() : 1;
                length = item.getBaseItem().getLength() > 0 ? item.getBaseItem().getLength() : 1;
            } else {
                width = item.getBaseItem().getLength() > 0 ? item.getBaseItem().getLength() : 1;
                length = item.getBaseItem().getWidth() > 0 ? item.getBaseItem().getWidth() : 1;
            }

            if (!(tile.getX() >= item.getX() && tile.getX() <= item.getX() + width - 1 && tile.getY() >= item.getY() && tile.getY() <= item.getY() + length - 1))
                continue;

            items.add(item);

            if (returnOnFirst) {
                return items;
            }
        }

        if (this.loaded) {
            this.tileCache.put(tile, items);
        }

        return items;
    }

    public THashSet<RoomItem> getItemsAt(int x, int y, double minZ) {
        THashSet<RoomItem> items = new THashSet<>();

        for (RoomItem item : this.getItemsAt(x, y)) {
            if (item.getZ() < minZ)
                continue;

            items.add(item);
        }
        return items;
    }

    public THashSet<RoomItem> getItemsAt(Class<? extends RoomItem> type, int x, int y) {
        THashSet<RoomItem> items = new THashSet<>();

        for (RoomItem item : this.getItemsAt(x, y)) {
            if (!item.getClass().equals(type))
                continue;

            items.add(item);
        }
        return items;
    }

    public boolean hasItemsAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short) x, (short) y);

        if (tile == null)
            return false;

        return !this.getItemsAt(tile, true).isEmpty();
    }

    public RoomItem getTopItemAt(RoomTile tile) {
        if(tile == null) {
            return null;
        }

        return this.getTopItemAt(tile.getX(), tile.getY(), null);
    }

    public RoomItem getTopItemAt(int x, int y) {
        return this.getTopItemAt(x, y, null);
    }

    public RoomItem getTopItemAt(int x, int y, RoomItem exclude) {
        RoomTile tile = this.getLayout().getTile((short) x, (short) y);

        if (tile == null)
            return null;

        RoomItem highestItem = null;

        for (RoomItem item : this.getItemsAt(x, y)) {
            if (exclude != null && exclude == item)
                continue;

            if (highestItem != null && highestItem.getZ() + Item.getCurrentHeight(highestItem) > item.getZ() + Item.getCurrentHeight(item))
                continue;

            highestItem = item;
        }

        return highestItem;
    }

    public RoomItem getTopItemAt(THashSet<RoomTile> tiles, RoomItem exclude) {
        RoomItem highestItem = null;
        for (RoomTile tile : tiles) {

            if (tile == null)
                continue;

            for (RoomItem item : this.getItemsAt(tile.getX(), tile.getY())) {
                if (exclude != null && exclude == item)
                    continue;

                if (highestItem != null && highestItem.getZ() + Item.getCurrentHeight(highestItem) > item.getZ() + Item.getCurrentHeight(item))
                    continue;

                highestItem = item;
            }
        }

        return highestItem;
    }

    public double getTopHeightAt(int x, int y) {
        RoomItem item = this.getTopItemAt(x, y);
        if (item != null) {
            return (item.getZ() + Item.getCurrentHeight(item) - (item.getBaseItem().allowSit() ? 1 : 0));
        } else {
            return this.layout.getHeightAtSquare(x, y);
        }
    }

    public RoomItem getLowestChair(RoomTile tile) {
        RoomItem lowestChair = null;

        THashSet<RoomItem> items = this.getItemsAt(tile);
        if (items != null && !items.isEmpty()) {
            for (RoomItem item : items) {

                if (!item.getBaseItem().allowSit())
                    continue;

                if (lowestChair != null && lowestChair.getZ() < item.getZ())
                    continue;

                lowestChair = item;
            }
        }

        return lowestChair;
    }

    public RoomItem getTallestChair(RoomTile tile) {
        RoomItem lowestChair = null;

        THashSet<RoomItem> items = this.getItemsAt(tile);
        if (items != null && !items.isEmpty()) {
            for (RoomItem item : items) {

                if (!item.getBaseItem().allowSit())
                    continue;

                if (lowestChair != null && lowestChair.getZ() + Item.getCurrentHeight(lowestChair) > item.getZ() + Item.getCurrentHeight(item))
                    continue;

                lowestChair = item;
            }
        }

        return lowestChair;
    }

    public double getStackHeight(short x, short y, boolean calculateHeightmap, RoomItem exclude) {

        if (x < 0 || y < 0 || this.layout == null)
            return calculateHeightmap ? Short.MAX_VALUE : 0.0;

        if (Emulator.getPluginManager().isRegistered(FurnitureStackHeightEvent.class, true)) {
            FurnitureStackHeightEvent event = Emulator.getPluginManager().fireEvent(new FurnitureStackHeightEvent(x, y, this));
            if (event.hasPluginHelper()) {
                return calculateHeightmap ? event.getHeight() * 256.0D : event.getHeight();
            }
        }

        double height = this.layout.getHeightAtSquare(x, y);
        boolean canStack = true;

        THashSet<RoomItem> stackHelpers = this.getItemsAt(InteractionStackHelper.class, x, y);


        for (RoomItem item : stackHelpers) {
            if (item == exclude) continue;
            return calculateHeightmap ? item.getZ() * 256.0D : item.getZ();
        }


        RoomItem item = this.getTopItemAt(x, y, exclude);
        if (item != null) {
            canStack = item.getBaseItem().allowStack();
            height = item.getZ() + (item.getBaseItem().allowSit() ? 0 : Item.getCurrentHeight(item));
        }

        if (calculateHeightmap) {
            return (canStack ? height * 256.0D : Short.MAX_VALUE);
        }

        return canStack ? height : -1;
    }

    public double getStackHeight(short x, short y, boolean calculateHeightmap) {
        return this.getStackHeight(x, y, calculateHeightmap, null);
    }

    public boolean hasObjectTypeAt(Class<?> type, int x, int y) {
        THashSet<RoomItem> items = this.getItemsAt(x, y);

        for (RoomItem item : items) {
            if (item.getClass() == type) {
                return true;
            }
        }

        return false;
    }

    public boolean canSitOrLayAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short) x, (short) y);

        if(tile == null) {
            return false;
        }

        if (this.roomUnitManager.hasHabbosAt(tile))
            return false;

        THashSet<RoomItem> items = this.getItemsAt(x, y);

        return this.canSitAt(items) || this.canLayAt(items);
    }

    public boolean canSitAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short) x, (short) y);

        if(tile == null) {
            return false;
        }

        if (this.roomUnitManager.hasHabbosAt(tile))
            return false;

        return this.canSitAt(this.getItemsAt(x, y));
    }

    boolean canWalkAt(RoomTile roomTile) {
        if (roomTile == null) {
            return false;
        }

        if (roomTile.getState() == RoomTileState.INVALID)
            return false;

        RoomItem topItem = null;
        boolean canWalk = true;
        THashSet<RoomItem> items = this.getItemsAt(roomTile);
        if (items != null) {
            for (RoomItem item : items) {
                if (topItem == null) {
                    topItem = item;
                }

                if (item.getZ() > topItem.getZ()) {
                    topItem = item;
                    canWalk = topItem.isWalkable() || topItem.getBaseItem().allowWalk();
                } else if (item.getZ() == topItem.getZ() && canWalk) {
                    if ((!topItem.isWalkable() && !topItem.getBaseItem().allowWalk())
                            || (!item.getBaseItem().allowWalk() && !item.isWalkable())) {
                        canWalk = false;
                    }
                }
            }
        }

        return canWalk;
    }

    boolean canSitAt(THashSet<RoomItem> items) {
        if (items == null)
            return false;

        RoomItem tallestItem = null;

        for (RoomItem item : items) {
            if (tallestItem != null && tallestItem.getZ() + Item.getCurrentHeight(tallestItem) > item.getZ() + Item.getCurrentHeight(item))
                continue;

            tallestItem = item;
        }

        if (tallestItem == null)
            return false;

        return tallestItem.getBaseItem().allowSit();
    }

    public boolean canLayAt(int x, int y) {
        return this.canLayAt(this.getItemsAt(x, y));
    }

    boolean canLayAt(THashSet<RoomItem> items) {
        if (items == null || items.isEmpty())
            return true;

        RoomItem topItem = null;

        for (RoomItem item : items) {
            if ((topItem == null || item.getZ() > topItem.getZ())) {
                topItem = item;
            }
        }

        return (topItem == null || topItem.getBaseItem().allowLay());
    }

    public RoomTile getRandomWalkableTile() {
        for (int i = 0; i < 10; i++) {
            RoomTile tile = this.layout.getTile((short) (Math.random() * this.layout.getMapSizeX()), (short) (Math.random() * this.layout.getMapSizeY()));
            if (tile != null && tile.getState() != RoomTileState.BLOCKED && tile.getState() != RoomTileState.INVALID) {
                return tile;
            }
        }

        return null;
    }

    public Habbo getHabbo(String username) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo.getHabboInfo().getUsername().equalsIgnoreCase(username))
                return habbo;
        }
        return null;
    }

    public Habbo getHabbo(RoomUnit roomUnit) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo.getRoomUnit() == roomUnit)
                return habbo;
        }
        return null;
    }

    public Habbo getHabboByRoomUnitId(int roomUnitId) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo.getRoomUnit().getVirtualId() == roomUnitId)
                return habbo;
        }

        return null;
    }

    public void sendComposer(ServerMessage message) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo.getClient() == null) continue;

            habbo.getClient().sendResponse(message);
        }
    }

    public void sendComposerToHabbosWithRights(ServerMessage message) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (this.hasRights(habbo)) {
                habbo.getClient().sendResponse(message);
            }
        }
    }

    public void petChat(ServerMessage message) {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (!habbo.getHabboStats().isIgnorePets())
                habbo.getClient().sendResponse(message);
        }
    }

    public void botChat(ServerMessage message) {
        if (message == null) {
            return;
        }

        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo == null) { return ; }
            if (!habbo.getHabboStats().isIgnoreBots())
                habbo.getClient().sendResponse(message);
        }
    }

    private void loadRights(Connection connection) {
        this.rights.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM room_rights WHERE room_id = ?")) {
            statement.setInt(1, this.roomInfo.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.rights.add(set.getInt(DatabaseConstants.USER_ID));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION, e);
        }
    }

    private void loadBans(Connection connection) {
        this.bannedHabbos.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, room_bans.* FROM room_bans INNER JOIN users ON room_bans.user_id = users.id WHERE ends > ? AND room_bans.room_id = ?")) {
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            statement.setInt(2, this.roomInfo.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.bannedHabbos.containsKey(set.getInt(DatabaseConstants.USER_ID)))
                        continue;

                    this.bannedHabbos.put(set.getInt(DatabaseConstants.USER_ID), new RoomBan(set));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public RoomRightLevels getGuildRightLevel(Habbo habbo) {
        if(!this.roomInfo.hasGuild()) {
            return RoomRightLevels.NONE;
        }

        if (habbo.getHabboStats().hasGuild(this.roomInfo.getGuild().getId())) {
            if (Emulator.getGameEnvironment().getGuildManager().getOnlyAdmins(this.roomInfo.getGuild()).get(habbo.getHabboInfo().getId()) != null)
                return RoomRightLevels.GUILD_ADMIN;

            if (this.roomInfo.getGuild().isRights()) {
                return RoomRightLevels.GUILD_RIGHTS;
            }
        }

        return RoomRightLevels.NONE;
    }

    public boolean hasRights(Habbo habbo) {
        return this.getRoomInfo().isRoomOwner(habbo) || this.rights.contains(habbo.getHabboInfo().getId()) || (habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE && this.roomUnitManager.getCurrentRoomHabbos().containsKey(habbo.getHabboInfo().getId()));
    }

    public void giveRights(Habbo habbo) {
        if (habbo != null) {
            this.giveRights(habbo.getHabboInfo().getId());
        }
    }

    public void giveRights(int userId) {
        if (this.rights.contains(userId))
            return;

        if (this.rights.add(userId)) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_rights VALUES (?, ?)")) {
                statement.setInt(1, this.roomInfo.getId());
                statement.setInt(2, userId);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        Habbo habbo = this.roomUnitManager.getRoomHabboById(userId);

        if (habbo != null) {
            this.refreshRightsForHabbo(habbo);

            this.sendComposer(new FlatControllerAddedComposer(this, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername()).compose());
        } else {
            Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.roomInfo.getOwnerInfo().getId());

            if (owner != null) {
                MessengerBuddy buddy = owner.getMessenger().getFriend(userId);

                if (buddy != null) {
                    this.sendComposer(new FlatControllerAddedComposer(this, userId, buddy.getUsername()).compose());
                }
            }
        }
    }

    public void removeRights(int userId) {
        Habbo habbo = this.roomUnitManager.getRoomHabboById(userId);

        if (Emulator.getPluginManager().fireEvent(new UserRightsTakenEvent(this.roomUnitManager.getRoomHabboById(this.roomInfo.getOwnerInfo().getId()), userId, habbo)).isCancelled())
            return;

        this.sendComposer(new FlatControllerRemovedComposer(this, userId).compose());

        if (this.rights.remove(userId)) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ? AND user_id = ?")) {
                statement.setInt(1, this.roomInfo.getId());
                statement.setInt(2, userId);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        if (habbo != null) {
            this.ejectUserFurni(habbo.getHabboInfo().getId());
            habbo.getRoomUnit().setRightsLevel(RoomRightLevels.NONE);
            habbo.getRoomUnit().removeStatus(RoomUnitStatus.FLAT_CONTROL);
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void removeAllRights() {
        for (int userId : rights.toArray()) {
            this.ejectUserFurni(userId);
        }

        this.rights.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?")) {
            statement.setInt(1, this.roomInfo.getId());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        this.refreshRightsInRoom();
    }

    void refreshRightsInRoom() {
        Room room = this;
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if (habbo.getRoomUnit().getRoom() == room) {
                this.refreshRightsForHabbo(habbo);
            }
        }
    }

    public void refreshRightsForHabbo(Habbo habbo) {
        RoomItem item;
        RoomRightLevels flatCtrl = RoomRightLevels.NONE;
        if (habbo.getHabboStats().isRentingSpace()) {
            item = this.getHabboItem(habbo.getHabboStats().getRentedItemId());

            if (item != null) {
                return;
            }
        }

        if (habbo.hasRight(Permission.ACC_ANYROOMOWNER) || this.getRoomInfo().isRoomOwner(habbo)) {
            habbo.getClient().sendResponse(new YouAreOwnerMessageComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        } else if (this.hasRights(habbo) && !this.roomInfo.hasGuild()) {
            flatCtrl = RoomRightLevels.RIGHTS;
        } else if (this.roomInfo.hasGuild()) {
            flatCtrl = this.getGuildRightLevel(habbo);
        }

        habbo.getClient().sendResponse(new YouAreControllerMessageComposer(flatCtrl));
        habbo.getRoomUnit().setStatus(RoomUnitStatus.FLAT_CONTROL, flatCtrl.getLevel() + "");
        habbo.getRoomUnit().setRightsLevel(flatCtrl);
        habbo.getRoomUnit().setStatusUpdateNeeded(true);

        if (flatCtrl.equals(RoomRightLevels.MODERATOR)) {
            habbo.getClient().sendResponse(new FlatControllersComposer(this));
        }
    }

    public THashMap<Integer, String> getUsersWithRights() {
        THashMap<Integer, String> rightsMap = new THashMap<>();

        if (!this.rights.isEmpty()) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS username, users.id as user_id FROM room_rights INNER JOIN users ON room_rights.user_id = users.id WHERE room_id = ?")) {
                statement.setInt(1, this.roomInfo.getId());
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        rightsMap.put(set.getInt(DatabaseConstants.USER_ID), set.getString("username"));
                    }
                }
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        return rightsMap;
    }

    public void unbanHabbo(int userId) {
        RoomBan ban = this.bannedHabbos.remove(userId);

        if (ban != null) {
            ban.delete();
        }

        this.sendComposer(new UserUnbannedFromRoomComposer(this, userId).compose());
    }

    public boolean isBanned(Habbo habbo) {
        RoomBan ban = this.bannedHabbos.get(habbo.getHabboInfo().getId());

        boolean banned = ban != null && ban.getEndTimestamp() > Emulator.getIntUnixTimestamp() && !habbo.hasRight(Permission.ACC_ANYROOMOWNER) && !habbo.hasRight(Permission.ACC_ENTERANYROOM);

        if (!banned && ban != null) {
            this.unbanHabbo(habbo.getHabboInfo().getId());
        }

        return banned;
    }

    public TIntObjectHashMap<RoomBan> getBannedHabbos() {
        return this.bannedHabbos;
    }

    public void addRoomBan(RoomBan roomBan) {
        this.bannedHabbos.put(roomBan.getUserId(), roomBan);
    }

    public void makeSit(Habbo habbo) {
        if (habbo.getRoomUnit() == null) return;

        if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT) || !habbo.getRoomUnit().canForcePosture()) {
            return;
        }

        habbo.getRoomUnit().setDance(DanceType.NONE);
        habbo.getRoomUnit().setCmdSitEnabled(true);
        habbo.getRoomUnit().setBodyRotation(RoomRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
        habbo.getRoomUnit().setStatus(RoomUnitStatus.SIT, 0.5 + "");
        this.sendComposer(new UserUpdateComposer(habbo.getRoomUnit()).compose());
    }

    public void makeStand(Habbo habbo) {
        if (habbo.getRoomUnit() == null) return;

        RoomItem item = this.getTopItemAt(habbo.getRoomUnit().getCurrentPosition().getX(), habbo.getRoomUnit().getCurrentPosition().getY());
        if (item == null || !item.getBaseItem().allowSit() || !item.getBaseItem().allowLay()) {
            habbo.getRoomUnit().setCmdStandEnabled(true);
            habbo.getRoomUnit().setBodyRotation(RoomRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
            habbo.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
            this.sendComposer(new UserUpdateComposer(habbo.getRoomUnit()).compose());
        }
    }

    public void giveEffect(Habbo habbo, int effectId, int duration, boolean ignoreChecks) {
        if (habbo != null && habbo.getRoomUnit() != null && this.roomUnitManager.getCurrentRoomHabbos().containsKey(habbo.getHabboInfo().getId())) {
            this.giveEffect(habbo.getRoomUnit(), effectId, duration, ignoreChecks);
        }
    }

    public void giveEffect(Habbo habbo, int effectId, int duration) {
        if (habbo != null && habbo.getRoomUnit() != null && this.roomUnitManager.getCurrentRoomHabbos().containsKey(habbo.getHabboInfo().getId())) {
            this.giveEffect(habbo.getRoomUnit(), effectId, duration, false);
        }
    }

    public void giveEffect(RoomUnit roomUnit, int effectId, int duration) {
        this.giveEffect(roomUnit, effectId, duration, false);
    }

    public void giveEffect(RoomUnit roomUnit, int effectId, int duration, boolean ignoreChecks) {
        if (roomUnit == null || !roomUnit.isInRoom() || !(roomUnit instanceof RoomAvatar roomAvatar)) {
            return;
        }

        Habbo habbo = this.getRoomUnitManager().getHabboByRoomUnit(roomAvatar);

        if (roomAvatar.getRoomUnitType() == RoomUnitType.HABBO && (habbo == null || habbo.getHabboInfo().isInGame() && !ignoreChecks)) {
            return;
        }

        if (duration == -1 || duration == Integer.MAX_VALUE) {
            duration = Integer.MAX_VALUE;
        } else {
            duration += Emulator.getIntUnixTimestamp();
        }

        if ((this.allowEffects || ignoreChecks) && !roomAvatar.isSwimming()) {
            roomAvatar.setEffectId(effectId);
            roomAvatar.setEffectEndTimestamp(duration);
            this.sendComposer(new AvatarEffectMessageComposer(roomAvatar).compose());
        }
    }

    public void updateItem(RoomItem item) {
        if (!this.isLoaded()) {
            return;
        }

        if (item != null && item.getRoomId() == this.roomInfo.getId() && item.getBaseItem() != null) {
            if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
                this.sendComposer(new ObjectUpdateMessageComposer(item).compose());
                this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
            } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
                this.sendComposer(new ItemUpdateMessageComposer(item).compose());
            }
        }

    }

    public void updateItemState(RoomItem item) {
        if (!item.isLimited()) {
            this.sendComposer(new OneWayDoorStatusMessageComposer(item).compose());
        } else {
            this.sendComposer(new ObjectUpdateMessageComposer(item).compose());
        }

        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            if (this.layout == null) return;

            this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));

            if (item instanceof InteractionMultiHeight interactionMultiHeight) {
                interactionMultiHeight.updateUnitsOnItem(this);
            }
        }
    }

    public int getUserFurniCount(int userId) {
        return this.furniOwnerCount.get(userId);
    }

    public int getUserUniqueFurniCount(int userId) {
        THashSet<Item> items = new THashSet<>();

        for (RoomItem item : this.roomItems.valueCollection()) {
            if (!items.contains(item.getBaseItem()) && item.getUserId() == userId) items.add(item.getBaseItem());
        }

        return items.size();
    }

    public void ejectUserFurni(int userId) {
        THashSet<RoomItem> items = new THashSet<>();

        TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; ) {
            try {
                iterator.advance();
            } catch (Exception e) {
                break;
            }

            if (iterator.value().getUserId() == userId) {
                items.add(iterator.value());
                iterator.value().setRoomId(0);
            }
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItems(items);
            habbo.getClient().sendResponse(new UnseenItemsComposer(items));
        }

        for (RoomItem i : items) {
            this.pickUpItem(i, null);
        }
    }

    public void ejectUserItem(RoomItem item) {
        this.pickUpItem(item, null);
    }

    public void ejectAll() {
        this.ejectAll(null);
    }

    public void ejectAll(Habbo habbo) {
        THashMap<Integer, THashSet<RoomItem>> userItemsMap = new THashMap<>();

        synchronized (this.roomItems) {
            TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; ) {
                try {
                    iterator.advance();
                } catch (Exception e) {
                    break;
                }

                if (habbo != null && iterator.value().getUserId() == habbo.getHabboInfo().getId())
                    continue;

                if (iterator.value() instanceof InteractionPostIt)
                    continue;

                userItemsMap.computeIfAbsent(iterator.value().getUserId(), k -> new THashSet<>()).add(iterator.value());
            }
        }

        for (Map.Entry<Integer, THashSet<RoomItem>> entrySet : userItemsMap.entrySet()) {
            for (RoomItem i : entrySet.getValue()) {
                this.pickUpItem(i, null);
            }

            Habbo user = Emulator.getGameEnvironment().getHabboManager().getHabbo(entrySet.getKey());

            if (user != null) {
                user.getInventory().getItemsComponent().addItems(entrySet.getValue());
                user.getClient().sendResponse(new UnseenItemsComposer(entrySet.getValue()));
            }
        }
    }

    public void refreshGuild(Guild guild) {
        if (guild.getRoomId() == this.roomInfo.getId()) {
            THashSet<GuildMember> members = Emulator.getGameEnvironment().getGuildManager().getGuildMembers(guild.getId());

            for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
                Optional<GuildMember> member = members.stream().filter(m -> m.getUserId() == habbo.getHabboInfo().getId()).findAny();

                if (member.isEmpty()) continue;

                habbo.getClient().sendResponse(new HabboGroupDetailsMessageComposer(guild, habbo.getClient(), false, member.get()));
            }
        }

        this.refreshGuildRightsInRoom();
    }

    public void refreshGuildColors(Guild guild) {
        if (guild.getRoomId() == this.roomInfo.getId()) {
            TIntObjectIterator<RoomItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; ) {
                try {
                    iterator.advance();
                } catch (Exception e) {
                    break;
                }

                RoomItem roomItem = iterator.value();

                if (roomItem instanceof InteractionGuildFurni interactionGuildFurni && interactionGuildFurni.getGuildId() == guild.getId()) {
                    this.updateItem(roomItem);
                }
            }
        }
    }

    public void refreshGuildRightsInRoom() {
        for (Habbo habbo : this.roomUnitManager.getRoomHabbos()) {
            if ((habbo.getRoomUnit().getRoom() == this && habbo.getHabboInfo().getId() != this.roomInfo.getOwnerInfo().getId())
                    && (!(habbo.hasRight(Permission.ACC_ANYROOMOWNER) || habbo.hasRight(Permission.ACC_MOVEROTATE))))
                this.refreshRightsForHabbo(habbo);
        }
    }

    public void idle(Habbo habbo) {
        habbo.getRoomUnit().setIdle();

        if (habbo.getRoomUnit().getDanceType() != DanceType.NONE) {
            habbo.getRoomUnit().setDance(DanceType.NONE);
        }

        this.sendComposer(new SleepMessageComposer(habbo.getRoomUnit()).compose());
        WiredHandler.handle(WiredTriggerType.IDLES, habbo.getRoomUnit(), this, new Object[]{habbo});
    }

    public void unIdle(Habbo habbo) {
        if (habbo == null || habbo.getRoomUnit() == null) return;
        habbo.getRoomUnit().resetIdleTimer();
        this.sendComposer(new SleepMessageComposer(habbo.getRoomUnit()).compose());
        WiredHandler.handle(WiredTriggerType.UNIDLES, habbo.getRoomUnit(), this, new Object[]{habbo});
    }

    public void addToWordFilter(String word) {
        synchronized (this.wordFilterWords) {
            if (this.wordFilterWords.contains(word))
                return;


            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO room_wordfilter VALUES (?, ?)")) {
                statement.setInt(1, this.roomInfo.getId());
                statement.setString(2, word);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
                return;
            }

            this.wordFilterWords.add(word);
        }
    }

    public void removeFromWordFilter(String word) {
        synchronized (this.wordFilterWords) {
            this.wordFilterWords.remove(word);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ? AND word = ?")) {
                statement.setInt(1, this.roomInfo.getId());
                statement.setString(2, word);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    public void handleWordQuiz(Habbo habbo, String answer) {
        synchronized (this.userVotes) {
            if (!this.wordQuiz.isEmpty() && !this.hasVotedInWordQuiz(habbo)) {
                answer = answer.replace(":", "");

                if (answer.equals("0")) {
                    this.noVotes++;
                } else if (answer.equals("1")) {
                    this.yesVotes++;
                }

                this.sendComposer(new QuestionAnsweredComposer(habbo.getHabboInfo().getId(), answer, this.noVotes, this.yesVotes).compose());
                this.userVotes.add(habbo.getHabboInfo().getId());
            }
        }
    }

    public void startWordQuiz(String question, int duration) {
        if (!this.hasActiveWordQuiz()) {
            this.wordQuiz = question;
            this.noVotes = 0;
            this.yesVotes = 0;
            this.userVotes.clear();
            this.wordQuizEnd = Emulator.getIntUnixTimestamp() + (duration / 1000);
            this.sendComposer(new QuestionComposer(duration, question).compose());
        }
    }

    public boolean hasActiveWordQuiz() {
        return Emulator.getIntUnixTimestamp() < this.wordQuizEnd;
    }

    public boolean hasVotedInWordQuiz(Habbo habbo) {
        return this.userVotes.contains(habbo.getHabboInfo().getId());
    }

    public void alert(String message) {
        this.sendComposer(new HabboBroadcastMessageComposer(message).compose());
    }

    public int itemCount() {
        return this.roomItems.size();
    }

    public void setHideWired(boolean hideWired) {
        this.roomInfo.setHiddenWiredEnabled(hideWired);

        if (this.roomInfo.isHiddenWiredEnabled()) {
            for (RoomItem item : this.roomSpecialTypes.getTriggers()) {
                this.sendComposer(new RemoveFloorItemComposer(item).compose());
            }

            for (RoomItem item : this.roomSpecialTypes.getEffects()) {
                this.sendComposer(new RemoveFloorItemComposer(item).compose());
            }

            for (RoomItem item : this.roomSpecialTypes.getConditions()) {
                this.sendComposer(new RemoveFloorItemComposer(item).compose());
            }

            for (RoomItem item : this.roomSpecialTypes.getExtras()) {
                this.sendComposer(new RemoveFloorItemComposer(item).compose());
            }
        } else {
            this.sendComposer(new ObjectsMessageComposer(this.furniOwnerNames, this.roomSpecialTypes.getTriggers()).compose());
            this.sendComposer(new ObjectsMessageComposer(this.furniOwnerNames, this.roomSpecialTypes.getEffects()).compose());
            this.sendComposer(new ObjectsMessageComposer(this.furniOwnerNames, this.roomSpecialTypes.getConditions()).compose());
            this.sendComposer(new ObjectsMessageComposer(this.furniOwnerNames, this.roomSpecialTypes.getExtras()).compose());
        }
    }

    public FurnitureMovementError canPlaceFurnitureAt(RoomItem item, Habbo habbo, RoomTile tile, int rotation) {
        if (this.itemCount() >= Room.MAXIMUM_FURNI) {
            return FurnitureMovementError.MAX_ITEMS;
        }

        if (tile == null || tile.getState() == RoomTileState.INVALID) {
            return FurnitureMovementError.INVALID_MOVE;
        }

        rotation %= 8;
        if (this.hasRights(habbo) || this.getGuildRightLevel(habbo).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS) || habbo.hasRight(Permission.ACC_MOVEROTATE)) {
            return FurnitureMovementError.NONE;
        }

        if (habbo.getHabboStats().isRentingSpace()) {
            RoomItem rentSpace = this.getHabboItem(habbo.getHabboStats().getRentedItemId());

            if (rentSpace != null) {
                if (!RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(tile.getX(), tile.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation))) {
                    return FurnitureMovementError.NO_RIGHTS;
                } else {
                    return FurnitureMovementError.NONE;
                }
            }
        }

        for (RoomItem area : this.getRoomSpecialTypes().getItemsOfType(InteractionBuildArea.class)) {
            if (((InteractionBuildArea) area).inSquare(tile) && ((InteractionBuildArea) area).isBuilder(habbo.getHabboInfo().getUsername())) {
                return FurnitureMovementError.NONE;
            }
        }

        return FurnitureMovementError.NO_RIGHTS;
    }

    public FurnitureMovementError furnitureFitsAt(RoomTile tile, RoomItem item, int rotation) {
        return furnitureFitsAt(tile, item, rotation, true);
    }

    public FurnitureMovementError furnitureFitsAt(RoomTile tile, RoomItem item, int rotation, boolean checkForUnits) {
        if (!this.layout.fitsOnMap(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation))
            return FurnitureMovementError.INVALID_MOVE;

        if (item instanceof InteractionStackHelper) return FurnitureMovementError.NONE;


        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        for (RoomTile t : occupiedTiles) {
            if (t.getState() == RoomTileState.INVALID) return FurnitureMovementError.INVALID_MOVE;
            if (!Emulator.getConfig().getBoolean("wired.place.under", false) || (Emulator.getConfig().getBoolean("wired.place.under", false) && !item.isWalkable() && !item.getBaseItem().allowSit() && !item.getBaseItem().allowLay())) {
                if (checkForUnits) {
                    if (this.roomUnitManager.hasHabbosAt(t)) return FurnitureMovementError.TILE_HAS_HABBOS;
                }
                if (checkForUnits) {
                    if (!this.roomUnitManager.getBotsAt(t).isEmpty()) return FurnitureMovementError.TILE_HAS_BOTS;
                }
                if (checkForUnits) {
                    if (this.roomUnitManager.hasPetsAt(t)) return FurnitureMovementError.TILE_HAS_PETS;
                }
            }
        }

        Optional<RoomItem> stackHelper = this.getItemsAt(tile).stream().filter(InteractionStackHelper.class::isInstance).findAny();

        List<Pair<RoomTile, THashSet<RoomItem>>> tileFurniList = new ArrayList<>();
        for (RoomTile t : occupiedTiles) {
            tileFurniList.add(Pair.create(t, this.getItemsAt(t)));

            RoomItem topItem = this.getTopItemAt(t.getX(), t.getY(), item);
            if (topItem != null && !topItem.getBaseItem().allowStack() && !t.getAllowStack()) {
                return FurnitureMovementError.CANT_STACK;
            }

            if ((stackHelper.isPresent() && item.getBaseItem().getInteractionType().getType() == InteractionWater.class) || topItem != null && (topItem.getBaseItem().getInteractionType().getType() == InteractionWater.class && (item.getBaseItem().getInteractionType().getType() == InteractionWater.class || item.getBaseItem().getInteractionType().getType() != InteractionWaterItem.class))) {
                return FurnitureMovementError.CANT_STACK;
            }
        }

        if (!item.canStackAt(this, tileFurniList)) {
            return FurnitureMovementError.CANT_STACK;
        }

        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError placeFloorFurniAt(RoomItem item, RoomTile tile, int rotation, Habbo owner) {
        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true)) {
            FurniturePlacedEvent event = Emulator.getPluginManager().fireEvent(new FurniturePlacedEvent(item, owner, tile));

            if (event.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_PLACE;
            }

            pluginHelper = event.hasPluginHelper();
        }

        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        FurnitureMovementError fits = furnitureFitsAt(tile, item, rotation);

        if (!fits.equals(FurnitureMovementError.NONE) && !pluginHelper) {
            return fits;
        }

        double height = tile.getStackHeight();

        for (RoomTile tile2 : occupiedTiles) {
            double sHeight = tile2.getStackHeight();
            if (sHeight > height) {
                height = sHeight;
            }
        }

        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true)) {
            FurnitureBuildheightEvent event = Emulator.getPluginManager().fireEvent(new FurnitureBuildheightEvent(item, owner, 0.00, height));
            if (event.hasChangedHeight()) {
                height = event.getUpdatedHeight();
            }
        }

        item.setZ(height);
        item.setX(tile.getX());
        item.setY(tile.getY());
        item.setRotation(rotation);
        if (!this.furniOwnerNames.containsKey(item.getUserId()) && owner != null) {
            this.furniOwnerNames.put(item.getUserId(), owner.getHabboInfo().getUsername());
        }

        item.needsUpdate(true);
        this.addHabboItem(item);
        item.setRoomId(this.roomInfo.getId());
        item.onPlace(this);
        this.updateTiles(occupiedTiles);
        this.sendComposer(new ObjectAddMessageComposer(item, this.getFurniOwnerName(item.getUserId())).compose());

        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(t);
            this.updateBotsAt(t.getX(), t.getY());
        }

        Emulator.getThreading().run(item);
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError placeWallFurniAt(RoomItem item, String wallPosition, Habbo owner) {
        if (!(this.hasRights(owner) || this.getGuildRightLevel(owner).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS))) {
            return FurnitureMovementError.NO_RIGHTS;
        }

        if (Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true)) {
            Event furniturePlacedEvent = new FurniturePlacedEvent(item, owner, null);
            Emulator.getPluginManager().fireEvent(furniturePlacedEvent);

            if (furniturePlacedEvent.isCancelled())
                return FurnitureMovementError.CANCEL_PLUGIN_PLACE;
        }

        item.setWallPosition(wallPosition);
        if (!this.furniOwnerNames.containsKey(item.getUserId()) && owner != null) {
            this.furniOwnerNames.put(item.getUserId(), owner.getHabboInfo().getUsername());
        }
        this.sendComposer(new ItemAddMessageComposer(item, this.getFurniOwnerName(item.getUserId())).compose());
        item.needsUpdate(true);
        this.addHabboItem(item);
        item.setRoomId(this.roomInfo.getId());
        item.onPlace(this);
        Emulator.getThreading().run(item);
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError moveFurniTo(RoomItem item, RoomTile tile, int rotation, Habbo actor) {
        return moveFurniTo(item, tile, rotation, actor, true, true);
    }

    public FurnitureMovementError moveFurniTo(RoomItem item, RoomTile tile, int rotation, Habbo actor, boolean sendUpdates) {
        return moveFurniTo(item, tile, rotation, actor, sendUpdates, true);
    }

    public FurnitureMovementError moveFurniTo(RoomItem item, RoomTile tile, int rotation, Habbo actor, boolean sendUpdates, boolean checkForUnits) {
        RoomTile oldLocation = this.layout.getTile(item.getX(), item.getY());

        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurnitureMovedEvent.class, true)) {
            FurnitureMovedEvent event = Emulator.getPluginManager().fireEvent(new FurnitureMovedEvent(item, actor, oldLocation, tile));
            if (event.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_MOVE;
            }
            pluginHelper = event.hasPluginHelper();
        }

        boolean magicTile = item instanceof InteractionStackHelper;

        Optional<RoomItem> stackHelper = this.getItemsAt(tile).stream().filter(InteractionStackHelper.class::isInstance).findAny();

        //Check if can be placed at new position
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        THashSet<RoomTile> newOccupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        RoomItem topItem = this.getTopItemAt(occupiedTiles, null);

        if ((stackHelper.isEmpty() && !pluginHelper) || item.getBaseItem().getInteractionType().getType() == InteractionWater.class) {
            if (oldLocation != tile) {
                for (RoomTile t : occupiedTiles) {
                    RoomItem tileTopItem = this.getTopItemAt(t.getX(), t.getY());
                    if (!magicTile && (tileTopItem != null && tileTopItem != item ? (t.getState().equals(RoomTileState.INVALID) || !t.getAllowStack() || !tileTopItem.getBaseItem().allowStack() ||
                            (tileTopItem.getBaseItem().getInteractionType().getType() == InteractionWater.class && (item.getBaseItem().getInteractionType().getType() != InteractionWaterItem.class || item.getBaseItem().getInteractionType().getType() == InteractionWater.class))) : this.calculateTileState(t, item).equals(RoomTileState.INVALID)) || stackHelper.isPresent() && item.getBaseItem().getInteractionType().getType() == InteractionWater.class) {
                        return FurnitureMovementError.CANT_STACK;
                    }

                    if (!Emulator.getConfig().getBoolean("wired.place.under", false) || (Emulator.getConfig().getBoolean("wired.place.under", false) && !item.isWalkable() && !item.getBaseItem().allowSit())) {
                        if (checkForUnits && !magicTile) {
                            if (this.roomUnitManager.hasHabbosAt(t))
                                return FurnitureMovementError.TILE_HAS_HABBOS;
                            if (!this.roomUnitManager.getBotsAt(t).isEmpty())
                                return FurnitureMovementError.TILE_HAS_BOTS;
                            if (this.roomUnitManager.hasPetsAt(t))
                                return FurnitureMovementError.TILE_HAS_PETS;
                        }
                    }
                }
            }

            List<Pair<RoomTile, THashSet<RoomItem>>> tileFurniList = new ArrayList<>();
            for (RoomTile t : occupiedTiles) {
                tileFurniList.add(Pair.create(t, this.getItemsAt(t)));
            }

            if (!magicTile && !item.canStackAt(this, tileFurniList)) {
                return FurnitureMovementError.CANT_STACK;
            }
        }

        THashSet<RoomTile> oldOccupiedTiles = this.layout.getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

        int oldRotation = item.getRotation();

        if (oldRotation != rotation) {
            item.setRotation(rotation);
            if (Emulator.getPluginManager().isRegistered(FurnitureRotatedEvent.class, true)) {
                Event furnitureRotatedEvent = new FurnitureRotatedEvent(item, actor, oldRotation);
                Emulator.getPluginManager().fireEvent(furnitureRotatedEvent);

                if (furnitureRotatedEvent.isCancelled()) {
                    item.setRotation(oldRotation);
                    return FurnitureMovementError.CANCEL_PLUGIN_ROTATE;
                }
            }

            if ((stackHelper.isEmpty() && topItem != null && topItem != item && !topItem.getBaseItem().allowStack()) || (topItem != null && topItem != item && topItem.getZ() + Item.getCurrentHeight(topItem) + Item.getCurrentHeight(item) > MAXIMUM_FURNI_HEIGHT)) {
                item.setRotation(oldRotation);
                return FurnitureMovementError.CANT_STACK;
            }

            // )
        }
        //Place at new position

        double height;

        if (stackHelper.isPresent()) {
            height = stackHelper.get().getExtradata().isEmpty() ? Double.parseDouble("0.0") : (Double.parseDouble(stackHelper.get().getExtradata()) / 100);
        } else if (item == topItem) {
            height = item.getZ();
        } else {
            height = this.getStackHeight(tile.getX(), tile.getY(), false, item);
            for (RoomTile til : occupiedTiles) {
                double sHeight = this.getStackHeight(til.getX(), til.getY(), false, item);
                if (sHeight > height) {
                    height = sHeight;
                }
            }
        }

        if (height > MAXIMUM_FURNI_HEIGHT) return FurnitureMovementError.CANT_STACK;
        if (height < this.getLayout().getHeightAtSquare(tile.getX(), tile.getY()))
            return FurnitureMovementError.CANT_STACK; //prevent furni going under the floor

        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true)) {
            FurnitureBuildheightEvent event = Emulator.getPluginManager().fireEvent(new FurnitureBuildheightEvent(item, actor, 0.00, height));
            if (event.hasChangedHeight()) {
                height = event.getUpdatedHeight();
            }
        }

        if (height > MAXIMUM_FURNI_HEIGHT) return FurnitureMovementError.CANT_STACK;
        if (height < this.getLayout().getHeightAtSquare(tile.getX(), tile.getY()))
            return FurnitureMovementError.CANT_STACK; //prevent furni going under the floor

        item.setX(tile.getX());
        item.setY(tile.getY());
        item.setZ(height);
        if (magicTile) {
            item.setZ(tile.getZ());
            item.setExtradata("" + item.getZ() * 100);
        }
        if (item.getZ() > MAXIMUM_FURNI_HEIGHT) {
            item.setZ(MAXIMUM_FURNI_HEIGHT);
        }


        //Update Furniture
        item.onMove(this, oldLocation, tile);
        item.needsUpdate(true);
        Emulator.getThreading().run(item);

        if (sendUpdates) {
            this.sendComposer(new ObjectUpdateMessageComposer(item).compose());
        }

        //Update old & new tiles
        occupiedTiles.removeAll(oldOccupiedTiles);
        occupiedTiles.addAll(oldOccupiedTiles);
        this.updateTiles(occupiedTiles);

        //Update Habbos at old position
        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(
                    t.getX(),
                    t.getY(),
                    new ArrayList<>(this.roomUnitManager.getHabbosAt(t))
                            /*.stream()
                            .filter(h -> !h.getRoomUnit().hasStatus(RoomUnitStatus.MOVE) || h.getRoomUnit().getGoal() == t)
                            .collect(Collectors.toCollection(THashSet::new))*/
            );
            this.updateBotsAt(t.getX(), t.getY());
        }
        if (Emulator.getConfig().getBoolean("wired.place.under", false)) {
            for (RoomTile t : newOccupiedTiles) {
                for (Habbo h : this.roomUnitManager.getHabbosAt(t)) {
                    try {
                        item.onWalkOn(h.getRoomUnit(), this, null);
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError slideFurniTo(RoomItem item, RoomTile tile, int rotation) {
        boolean magicTile = item instanceof InteractionStackHelper;

        //Check if can be placed at new position
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        List<Pair<RoomTile, THashSet<RoomItem>>> tileFurniList = new ArrayList<>();
        for (RoomTile t : occupiedTiles) {
            tileFurniList.add(Pair.create(t, this.getItemsAt(t)));
        }

        if (!magicTile && !item.canStackAt(this, tileFurniList)) {
            return FurnitureMovementError.CANT_STACK;
        }

        item.setRotation(rotation);

        //Place at new position
        if (magicTile) {
            item.setZ(tile.getZ());
            item.setExtradata("" + item.getZ() * 100);
        }
        if (item.getZ() > MAXIMUM_FURNI_HEIGHT) {
            item.setZ(MAXIMUM_FURNI_HEIGHT);
        }
        double offset = this.getStackHeight(tile.getX(), tile.getY(), false, item) - item.getZ();
        this.sendComposer(new FloorItemOnRollerComposer(item, null, tile, offset, this).compose());

        //Update Habbos at old position
        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(t);
            this.updateBotsAt(t.getX(), t.getY());
        }
        return FurnitureMovementError.NONE;
    }

    public TIntObjectMap<Habbo> getHabboQueue() {
        return this.habboQueue;
    }

    public TIntArrayList getRights() {
        return this.rights;
    }

    public ConcurrentSet<Game> getGames() {
        return this.games;
    }

    public TIntObjectMap<String> getFurniOwnerNames() {
        return this.furniOwnerNames;
    }

    public THashSet<String> getWordFilterWords() {
        return this.wordFilterWords;
    }

    public String getWordQuiz() {
        return this.wordQuiz;
    }

    public int getNoVotes() {
        return this.noVotes;
    }

    public int getYesVotes() {
        return this.yesVotes;
    }

    public int getWordQuizEnd() {
        return this.wordQuizEnd;
    }

    public RoomLayout getLayout() {
        return this.layout;
    }

    public boolean isAllowBotsWalk() {
        return this.allowBotsWalk;
    }

    public RoomPromotion getPromotion() {
        return this.promotion;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isPreLoaded() {
        return this.preLoaded;
    }

    public int getLastTimerReset() {
        return this.lastTimerReset;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public RoomSpecialTypes getRoomSpecialTypes() {
        return this.roomSpecialTypes;
    }

    public TraxManager getTraxManager() {
        return this.traxManager;
    }

    public long getCycleTimestamp() {
        return this.cycleTimestamp;
    }

    public HashMap<RoomTile, InteractionWiredTrigger> getTriggersOnRoom() {
        return this.triggersOnRoom;
    }

    public void setLayout(RoomLayout layout) {
        this.layout = layout;
    }

    public void setAllowBotsWalk(boolean allowBotsWalk) {
        this.allowBotsWalk = allowBotsWalk;
    }

    public void setAllowEffects(boolean allowEffects) {
        this.allowEffects = allowEffects;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public void setLastTimerReset(int lastTimerReset) {
        this.lastTimerReset = lastTimerReset;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }
}
