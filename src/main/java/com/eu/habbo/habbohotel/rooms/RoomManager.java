package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.RoomUserPetComposer;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.football.FootballGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.tag.BunnyrunGame;
import com.eu.habbo.habbohotel.games.tag.IceTagGame;
import com.eu.habbo.habbohotel.games.tag.RollerskateGame;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterComparator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.NavigatorManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.PetData;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollManager;
import com.eu.habbo.habbohotel.users.*;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.users.NewUserExperienceScriptProceedEvent;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorComposer;
import com.eu.habbo.messages.outgoing.hotelview.CloseConnectionMessageComposer;
import com.eu.habbo.messages.outgoing.polls.PollOfferComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionFinishedComposer;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.messages.outgoing.rooms.items.ItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ObjectsMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.promotions.RoomEventComposer;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.RemainingMutePeriodComposer;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomCreatedEvent;
import com.eu.habbo.plugin.events.rooms.RoomFloorItemsLoadEvent;
import com.eu.habbo.plugin.events.rooms.RoomUncachedEvent;
import com.eu.habbo.plugin.events.rooms.UserVoteRoomEvent;
import com.eu.habbo.plugin.events.users.HabboAddedToRoomEvent;
import com.eu.habbo.plugin.events.users.UserEnterRoomEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
public class RoomManager {

    //Configuration. Loaded from database & updated accordingly.
    public static int MAXIMUM_ROOMS_USER = 25;
    public static int MAXIMUM_ROOMS_HC = 35;
    public static int HOME_ROOM_ID = 0;
    public static boolean SHOW_PUBLIC_IN_POPULAR_TAB = false;
    @Getter
    private final THashMap<Integer, RoomCategory> roomCategories;
    private final List<String> mapNames;
    private final ConcurrentHashMap<Integer, Room> activeRooms;
    @Getter
    private final ArrayList<Class<? extends Game>> gameTypes;

    public RoomManager() {
        long millis = System.currentTimeMillis();
        this.roomCategories = new THashMap<>();
        this.mapNames = new ArrayList<>();
        this.activeRooms = new ConcurrentHashMap<>();
        this.loadRoomCategories();
        this.loadRoomModels();

        this.gameTypes = new ArrayList<>();

        registerGameType(BattleBanzaiGame.class);
        registerGameType(FreezeGame.class);
        registerGameType(WiredGame.class);
        registerGameType(FootballGame.class);
        registerGameType(BunnyrunGame.class);
        registerGameType(IceTagGame.class);
        registerGameType(RollerskateGame.class);

        log.info("Room Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadRoomModels() {
        this.mapNames.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM room_models")) {
            while (set.next()) {
                this.mapNames.add(set.getString("name"));
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public CustomRoomLayout loadCustomLayout(Room room) {
        CustomRoomLayout layout = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_models_custom WHERE id = ? LIMIT 1")) {
            statement.setInt(1, room.getId());
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    layout = new CustomRoomLayout(set, room);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return layout;
    }

    private void loadRoomCategories() {
        this.roomCategories.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM navigator_flatcats")) {
            while (set.next()) {
                this.roomCategories.put(set.getInt("id"), new RoomCategory(set));
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public void loadPublicRooms() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE is_public = ? OR is_staff_picked = ? ORDER BY id DESC")) {
            statement.setString(1, "1");
            statement.setString(2, "1");
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Room room = new Room(set);
                    room.preventUncaching = true;
                    this.activeRooms.put(set.getInt("id"), room);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public THashMap<Integer, List<Room>> findRooms(NavigatorFilterField filterField, String value, int category, boolean showInvisible) {
        THashMap<Integer, List<Room>> rooms = new THashMap<>();
        String query = filterField.getDatabaseQuery() + " AND rooms.state NOT LIKE " + (showInvisible ? "''" : "'invisible'") + (category >= 0 ? "AND rooms.category = '" + category + "'" : "") + "  ORDER BY rooms.users, rooms.id DESC LIMIT " + NavigatorManager.MAXIMUM_RESULTS_PER_PAGE;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, (filterField.getComparator() == NavigatorFilterComparator.EQUALS ? value : "%" + value + "%"));
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Room room = this.activeRooms.get(set.getInt("id"));

                    if (room == null) {
                        room = new Room(set);
                        this.activeRooms.put(set.getInt("id"), room);
                    }

                    if (!rooms.containsKey(set.getInt("category"))) {
                        rooms.put(set.getInt("category"), new ArrayList<>());
                    }

                    rooms.get(set.getInt("category")).add(room);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public RoomCategory getCategory(int id) {
        return roomCategories.values().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public RoomCategory getCategory(String name) {
        return roomCategories.values().stream().filter(c -> c.getCaption().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public RoomCategory getCategoryBySafeCaption(String safeCaption) {
        return roomCategories.values().stream().filter(c -> c.getCaptionSave().equalsIgnoreCase(safeCaption)).findFirst().orElse(null);
    }

    public List<RoomCategory> roomCategoriesForHabbo(Habbo habbo) {
        List<RoomCategory> categories = new ArrayList<>();
        for (RoomCategory category : this.roomCategories.values()) {
            if (category.getMinRank() <= habbo.getHabboInfo().getPermissionGroup().getId())
                categories.add(category);
        }

        Collections.sort(categories);

        return categories;
    }

    public boolean hasCategory(int categoryId, Habbo habbo) {
        return roomCategories.values().stream().anyMatch(c -> c.getId() == categoryId && c.getMinRank() <= habbo.getHabboInfo().getPermissionGroup().getId());
    }

    public List<Room> getRoomsByScore() {
        List<Room> rooms = new ArrayList<>(this.activeRooms.values());
        rooms.sort(Room.SORT_SCORE);

        return rooms;
    }

    public List<Room> getActiveRooms(int categoryId) {
        List<Room> rooms = new ArrayList<>();
        for (Room room : this.activeRooms.values()) {
            if (categoryId == room.getCategory() || categoryId == -1)
                rooms.add(room);
        }
        Collections.sort(rooms);
        return rooms;
    }

    //TODO Move to HabboInfo class.
    public ArrayList<Room> getRoomsForHabbo(Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList<>();
        for (Room room : this.activeRooms.values()) {
            if (room.getOwnerId() == habbo.getHabboInfo().getId())
                rooms.add(room);
        }
        rooms.sort(Room.SORT_ID);
        return rooms;
    }

    public ArrayList<Room> getRoomsForHabbo(String username) {
        Habbo h = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);
        if (h != null) {
            return this.getRoomsForHabbo(h);
        }

        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE owner_name = ? ORDER BY id DESC LIMIT 25")) {
            statement.setString(1, username);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    rooms.add(this.loadRoom(set.getInt("id")));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public Room loadRoom(int id) {
        return loadRoom(id, false);
    }

    public Room loadRoom(int id, boolean loadData) {
        Room room = null;

        if (this.activeRooms.containsKey(id)) {
            room = this.activeRooms.get(id);

            if (loadData && (room.isPreLoaded() && !room.isLoaded())) {
                room.loadData();
            }

            return room;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE id = ? LIMIT 1")) {
            statement.setInt(1, id);

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    room = new Room(set);
                    if (loadData) {
                        room.loadData();
                    }
                }
            }

            if (room != null) {
                this.activeRooms.put(room.getId(), room);
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return room;
    }


    public Room createRoom(int ownerId, String ownerName, String name, String description, String modelName, int usersMax, int categoryId, int tradeType) {
        Room room = null;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO rooms (owner_id, owner_name, name, description, model, users_max, category, trade_mode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, ownerId);
            statement.setString(2, ownerName);
            statement.setString(3, name);
            statement.setString(4, description);
            statement.setString(5, modelName);
            statement.setInt(6, usersMax);
            statement.setInt(7, categoryId);
            statement.setInt(8, tradeType);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next())
                    room = this.loadRoom(set.getInt(1));
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return room;
    }


    public Room createRoomForHabbo(Habbo habbo, String name, String description, String modelName, int usersMax, int categoryId, int tradeType) {
        Room room = this.createRoom(habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername(), name, description, modelName, usersMax, categoryId, tradeType);

        Emulator.getPluginManager().fireEvent(new NavigatorRoomCreatedEvent(habbo, room));

        return room;
    }

    public void loadRoomsForHabbo(Habbo habbo) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE owner_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (!this.activeRooms.containsKey(set.getInt("id")))
                        this.activeRooms.put(set.getInt("id"), new Room(set));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public void unloadRoomsForHabbo(Habbo habbo) {
        List<Room> roomsToDispose = new ArrayList<>();
        for (Room room : this.activeRooms.values()) {
            if (!room.isPublicRoom() && !room.isStaffPromotedRoom() && room.getOwnerId() == habbo.getHabboInfo().getId() && room.getUserCount() == 0 && (this.roomCategories.get(room.getCategory()) == null || !this.roomCategories.get(room.getCategory()).isPublic())) {
                roomsToDispose.add(room);
            }
        }

        for (Room room : roomsToDispose) {
            if (Emulator.getPluginManager().fireEvent(new RoomUncachedEvent(room)).isCancelled())
                continue;

            room.dispose();
            this.activeRooms.remove(room.getId());
        }
    }

    public void clearInactiveRooms() {
        THashSet<Room> roomsToDispose = new THashSet<>();
        for (Room room : this.activeRooms.values()) {
            if (!room.isPublicRoom() && !room.isStaffPromotedRoom() && !Emulator.getGameServer().getGameClientManager().containsHabbo(room.getOwnerId()) && room.isPreLoaded()) {
                roomsToDispose.add(room);
            }
        }

        for (Room room : roomsToDispose) {
            room.dispose();
            if (room.getUserCount() == 0)
                this.activeRooms.remove(room.getId());
        }
    }

    public boolean layoutExists(String name) {
        return this.mapNames.contains(name);
    }

    public RoomLayout loadLayout(String name, Room room) {
        RoomLayout layout = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_models WHERE name LIKE ? LIMIT 1")) {
            statement.setString(1, name);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    layout = new RoomLayout(set, room);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return layout;
    }

    public void unloadRoom(Room room) {
        room.dispose();
    }

    public void uncacheRoom(Room room) {
        this.activeRooms.remove(room.getId());
    }

    public void voteForRoom(Habbo habbo, Room room) {
        if (habbo.getHabboInfo().getCurrentRoom() != null && room != null && habbo.getHabboInfo().getCurrentRoom() == room) {
            if (this.hasVotedForRoom(habbo, room))
                return;

            UserVoteRoomEvent event = new UserVoteRoomEvent(room, habbo);
            if (Emulator.getPluginManager().fireEvent(event).isCancelled()) return;

            room.setScore(room.getScore() + 1);
            room.setNeedsUpdate(true);
            habbo.getHabboStats().getVotedRooms().push(room.getId());
            for (Habbo h : room.getHabbos()) {
                h.getClient().sendResponse(new RoomRatingComposer(room.getScore(), !this.hasVotedForRoom(h, room)));
            }

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_votes VALUES (?, ?)")) {
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, room.getId());
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    boolean hasVotedForRoom(Habbo habbo, Room room) {
        if (room.getOwnerId() == habbo.getHabboInfo().getId())
            return true;

        for (int i : habbo.getHabboStats().getVotedRooms().toArray()) {
            if (i == room.getId())
                return true;
        }

        return false;
    }

    public Room getRoom(int roomId) {
        return this.activeRooms.get(roomId);
    }

    public List<Room> getActiveRooms() {
        return new ArrayList<>(this.activeRooms.values());
    }

    public int loadedRoomsCount() {
        return this.activeRooms.size();
    }

    public void enterRoom(Habbo habbo, int roomId, String password) {
        this.enterRoom(habbo, roomId, password, false, null);
    }

    public void enterRoom(Habbo habbo, int roomId, String password, boolean overrideChecks) {
        this.enterRoom(habbo, roomId, password, overrideChecks, null);
    }

    public void enterRoom(Habbo habbo, int roomId, String password, boolean overrideChecks, RoomTile doorLocation) {
        Room room = this.loadRoom(roomId, true);

        if (room == null)
            return;

        if (habbo.getHabboInfo().getLoadingRoom() != 0 && room.getId() != habbo.getHabboInfo().getLoadingRoom()) {
            habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
            habbo.getHabboInfo().setLoadingRoom(0);
            return;
        }

        if (Emulator.getPluginManager().fireEvent(new UserEnterRoomEvent(habbo, room)).isCancelled()
                && habbo.getHabboInfo().getCurrentRoom() == null) {
            habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
            habbo.getHabboInfo().setLoadingRoom(0);
            return;
        }

        if (room.isBanned(habbo) && !habbo.hasRight(Permission.ACC_ANYROOMOWNER) && !habbo.hasRight(Permission.ACC_ENTERANYROOM)) {
            habbo.getClient().sendResponse(new CantConnectMessageComposer(CantConnectMessageComposer.ROOM_ERROR_BANNED));
            return;
        }

        if (habbo.getHabboInfo().getRoomQueueId() != roomId) {
            Room queRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if (queRoom != null) {
                queRoom.removeFromQueue(habbo);
            }
        }

        if (overrideChecks ||
                room.isOwner(habbo) ||
                room.getState() == RoomState.OPEN ||
                habbo.hasRight(Permission.ACC_ANYROOMOWNER) ||
                habbo.hasRight(Permission.ACC_ENTERANYROOM) ||
                room.hasRights(habbo) ||
                (room.getState().equals(RoomState.INVISIBLE) && room.hasRights(habbo)) ||
                (room.hasGuild() && room.getGuildRightLevel(habbo).isGreaterThan(RoomRightLevels.GUILD_RIGHTS))) {
            this.openRoom(habbo, room, doorLocation);
        } else if (room.getState() == RoomState.LOCKED) {
            boolean rightsFound = false;

            synchronized (room.roomUnitLock) {
                for (Habbo current : room.getHabbos()) {
                    if (room.hasRights(current) || current.getHabboInfo().getId() == room.getOwnerId() || (room.hasGuild() && room.getGuildRightLevel(current).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS))) {
                        current.getClient().sendResponse(new DoorbellMessageComposer(habbo.getHabboInfo().getUsername()));
                        rightsFound = true;
                    }
                }
            }

            if (!rightsFound) {
                habbo.getClient().sendResponse(new FlatAccessDeniedMessageComposer(""));
                habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
                habbo.getHabboInfo().setLoadingRoom(0);
                return;
            }

            habbo.getHabboInfo().setRoomQueueId(roomId);
            habbo.getClient().sendResponse(new DoorbellMessageComposer(""));
            room.addToQueue(habbo);
        } else if (room.getState() == RoomState.PASSWORD) {
            if (room.getPassword().equalsIgnoreCase(password))
                this.openRoom(habbo, room, doorLocation);
            else {
                habbo.getClient().sendResponse(new GenericErrorComposer(GenericErrorComposer.WRONG_PASSWORD_USED));
                habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
                habbo.getHabboInfo().setLoadingRoom(0);
            }
        } else {
            habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
            habbo.getHabboInfo().setLoadingRoom(0);
        }
    }

    void openRoom(Habbo habbo, Room room, RoomTile doorLocation) {
        if (room == null || room.getLayout() == null)
            return;

        if (Emulator.getConfig().getBoolean("hotel.room.enter.logs")) {
            this.logEnter(habbo, room);
        }

        if (habbo.getHabboInfo().getRoomQueueId() > 0) {
            Room r = Emulator.getGameEnvironment().getRoomManager().getRoom(habbo.getHabboInfo().getRoomQueueId());

            if (r != null) {
                r.removeFromQueue(habbo);
            }
        }

        habbo.getHabboInfo().setRoomQueueId(0);
        habbo.getClient().sendResponse(new FlatAccessibleMessageComposer(""));

        if (habbo.getRoomUnit() != null) {
            RoomUnit existingRoom = habbo.getRoomUnit();
            if (existingRoom.getRoom() != null) {
                if (existingRoom.getCurrentLocation() != null)
                    existingRoom.getCurrentLocation().removeUnit(existingRoom);
                existingRoom.getRoom().sendComposer(new UserRemoveMessageComposer(existingRoom).compose());
            }
            habbo.getRoomUnit().setRoom(null);
        }

        habbo.setRoomUnit(new RoomUnit());

        habbo.getRoomUnit().clearStatus();
        if (habbo.getRoomUnit().getCurrentLocation() == null) {
            habbo.getRoomUnit().setLocation(doorLocation != null ? doorLocation : room.getLayout().getDoorTile());
            if (habbo.getRoomUnit().getCurrentLocation() != null)
                habbo.getRoomUnit().setZ(habbo.getRoomUnit().getCurrentLocation().getStackHeight());

            if (doorLocation == null) {
                habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
                habbo.getRoomUnit().setHeadRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
            } else {
                habbo.getRoomUnit().setCanLeaveRoomByDoor(false);
                habbo.getRoomUnit().setTeleporting(true);
                HabboItem topItem = room.getTopItemAt(doorLocation.getX(), doorLocation.getY());
                if (topItem != null) {
                    habbo.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                }
            }
        }

        habbo.getRoomUnit().setRoomUnitType(RoomUnitType.USER);
        if (room.isBanned(habbo)) {
            habbo.getClient().sendResponse(new CantConnectMessageComposer(CantConnectMessageComposer.ROOM_ERROR_BANNED));
            return;
        }

        if (room.getUserCount() >= room.getUsersMax() && !habbo.hasRight(Permission.ACC_FULLROOMS) && !room.hasRights(habbo)) {
            habbo.getClient().sendResponse(new CantConnectMessageComposer(CantConnectMessageComposer.ROOM_ERROR_GUESTROOM_FULL));
            return;
        }

        habbo.getRoomUnit().clearStatus();
        habbo.getRoomUnit().setCmdTeleport(false);

        habbo.getClient().sendResponse(new OpenConnectionMessageComposer());

        habbo.getRoomUnit().setInRoom(true);
        if (habbo.getHabboInfo().getCurrentRoom() != room && habbo.getHabboInfo().getCurrentRoom() != null) {
            habbo.getHabboInfo().getCurrentRoom().removeHabbo(habbo, true);
        } else if (!habbo.getHabboStats().isBlockFollowing() && habbo.getHabboInfo().getCurrentRoom() == null) {
            habbo.getMessenger().connectionChanged(habbo, true, true);
        }

        if (habbo.getHabboInfo().getLoadingRoom() != 0) {
            Room oldRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(habbo.getHabboInfo().getLoadingRoom());
            if (oldRoom != null) {
                oldRoom.removeFromQueue(habbo);
            }
        }

        habbo.getHabboInfo().setLoadingRoom(room.getId());

        habbo.getClient().sendResponse(new RoomReadyMessageComposer(room));

        if (!room.getWallPaint().equals("0.0"))
            habbo.getClient().sendResponse(new RoomPropertyMessageComposer("wallpaper", room.getWallPaint()));

        if (!room.getFloorPaint().equals("0.0"))
            habbo.getClient().sendResponse(new RoomPropertyMessageComposer("floor", room.getFloorPaint()));

        habbo.getClient().sendResponse(new RoomPropertyMessageComposer("landscape", room.getBackgroundPaint()));

        room.refreshRightsForHabbo(habbo);

        habbo.getClient().sendResponse(new RoomRatingComposer(room.getScore(), !this.hasVotedForRoom(habbo, room)));

        habbo.getRoomUnit().setFastWalk(habbo.getRoomUnit().isFastWalk() && habbo.hasCommand("cmd_fastwalk", room.hasRights(habbo)));

        if (room.isPromoted()) {
            habbo.getClient().sendResponse(new RoomEventComposer(room, room.getPromotion()));
        } else {
            habbo.getClient().sendResponse(new RoomEventComposer(null, null));
        }

        if (room.getOwnerId() != habbo.getHabboInfo().getId() && !habbo.getHabboStats().visitedRoom(room.getId())) {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomEntry"));
        }
    }

    public void enterRoom(final Habbo habbo, final Room room) {
        if (habbo.getHabboInfo().getLoadingRoom() != room.getId()) {
            if (habbo.getHabboInfo().getLoadingRoom() != 0) {
                habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
            }
            return;
        }

        habbo.getRoomUnit()
                .removeStatus(RoomUnitStatus.FLAT_CONTROL)
                .setPathFinderRoom(room)
                .setHandItem(0)
                .setRightsLevel(RoomRightLevels.NONE);

        habbo.getHabboInfo().setLoadingRoom(0)
                .setCurrentRoom(room);

        room.refreshRightsForHabbo(habbo);
        if (habbo.getRoomUnit().isKicked() && !habbo.getRoomUnit().canWalk()) {
            habbo.getRoomUnit().setCanWalk(true);
        }
        habbo.getRoomUnit().setKicked(false);

        if (habbo.getRoomUnit().getCurrentLocation() == null && !habbo.getRoomUnit().isTeleporting()) {
            RoomTile doorTile = room.getLayout().getTile(room.getLayout().getDoorX(), room.getLayout().getDoorY());

            if (doorTile != null) {
                habbo.getRoomUnit().setLocation(doorTile);
                habbo.getRoomUnit().setZ(doorTile.getStackHeight());
            }

            habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
            habbo.getRoomUnit().setHeadRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
        }

        habbo.getRoomUnit().setPathFinderRoom(room);
        habbo.getRoomUnit().resetIdleTimer();

        habbo.getRoomUnit().setInvisible(false);
        room.addHabbo(habbo);

        List<Habbo> habbos = new ArrayList<>();
        if (!room.getCurrentHabbos().isEmpty()) {

            Collection<Habbo> habbosToSendEnter = room.getCurrentHabbos().values();
            Collection<Habbo> visibleHabbos = room.getHabbos();

            if (Emulator.getPluginManager().isRegistered(HabboAddedToRoomEvent.class, false)) {
                HabboAddedToRoomEvent event = Emulator.getPluginManager().fireEvent(new HabboAddedToRoomEvent(habbo, room, habbosToSendEnter, visibleHabbos));
                habbosToSendEnter = event.getHabbosToSendEnter();
                visibleHabbos = event.getVisibleHabbos();
            }

            habbosToSendEnter.stream().map(Habbo::getClient).filter(Objects::nonNull).forEach(client -> {
                client.sendResponse(new RoomUsersComposer(habbo).compose());
                client.sendResponse(new UserUpdateComposer(habbo.getRoomUnit()).compose());
            });

            habbos = visibleHabbos.stream().filter(h -> !h.getRoomUnit().isInvisible()).toList();

            synchronized (room.roomUnitLock) {
                habbo.getClient().sendResponse(new RoomUsersComposer(habbos));
                habbo.getClient().sendResponse(new UserUpdateComposer(habbos));
            }

            if (habbo.getHabboStats().getGuild() != 0) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(habbo.getHabboStats().getGuild());

                if (guild != null) {
                    room.sendComposer(new HabboAddGroupBadgesMessageComposer(guild).compose());
                }
            }

            int effect = habbo.getInventory().getEffectsComponent().getActivatedEffect();
            room.giveEffect(habbo.getRoomUnit(), effect, -1);
        }


        habbo.getClient().sendResponse(new RoomUsersComposer(room.getCurrentBots().valueCollection(), true));
        if (!room.getCurrentBots().isEmpty()) {
            room.getCurrentBots().valueCollection().stream()
                    .filter(b -> !b.getRoomUnit().getDanceType().equals(DanceType.NONE))
                    .forEach(b -> habbo.getClient().sendResponse(new DanceMessageComposer(b.getRoomUnit())));

            room.getCurrentBots().valueCollection()
                    .forEach(b -> habbo.getClient().sendResponse(new UserUpdateComposer(b.getRoomUnit(), b.getRoomUnit().getZ())));
        }

        habbo.getClient().sendResponse(new RoomEntryInfoMessageComposer(room, room.isOwner(habbo)));
        habbo.getClient().sendResponse(new RoomVisualizationSettingsComposer(room));
        habbo.getClient().sendResponse(new GetGuestRoomResultComposer(room, habbo.getClient().getHabbo(), false, true));
        habbo.getClient().sendResponse(new ItemsComposer(room));
        final THashSet<HabboItem> floorItems = new THashSet<>();

        THashSet<HabboItem> allFloorItems = new THashSet<>(room.getFloorItems());

        if (Emulator.getPluginManager().isRegistered(RoomFloorItemsLoadEvent.class, true)) {
            RoomFloorItemsLoadEvent roomFloorItemsLoadEvent = Emulator.getPluginManager().fireEvent(new RoomFloorItemsLoadEvent(habbo, allFloorItems));
            if (roomFloorItemsLoadEvent.hasChangedFloorItems()) {
                allFloorItems = roomFloorItemsLoadEvent.getFloorItems();
            }
        }

        allFloorItems.forEach(object -> {
            if (room.isHideWired() && object instanceof InteractionWired)
                return true;

            floorItems.add(object);
            if (floorItems.size() == 250) {
                habbo.getClient().sendResponse(new ObjectsMessageComposer(room.getFurniOwnerNames(), floorItems));
                floorItems.clear();
            }

            return true;
        });

        habbo.getClient().sendResponse(new ObjectsMessageComposer(room.getFurniOwnerNames(), floorItems));
        floorItems.clear();

        if (!room.getCurrentPets().isEmpty()) {
            habbo.getClient().sendResponse(new RoomPetComposer(room.getCurrentPets()));
            room.getCurrentPets().valueCollection().forEach(pet -> habbo.getClient().sendResponse(new UserUpdateComposer(pet.getRoomUnit())));
        }

        if (!habbo.getHabboStats().allowTalk()) {
            habbo.getHabboStats().setMutedBubbleTracker(true);
            int remainingMuteTime = habbo.getHabboStats().remainingMuteTime();
            habbo.getClient().sendResponse(new FloodControlMessageComposer(remainingMuteTime));
            habbo.getClient().sendResponse(new RemainingMutePeriodComposer(remainingMuteTime));
            room.sendComposer(new IgnoreResultMessageComposer(habbo, IgnoreResultMessageComposer.MUTED).compose());
        } else if (habbo.getHabboStats().isMutedBubbleTracker()) {
            habbo.getHabboStats().setMutedBubbleTracker(false);
        }

        THashMap<Integer, String> guildBadges = new THashMap<>();
        for (Habbo roomHabbo : habbos) {
            if (roomHabbo.getRoomUnit().getDanceType().getType() > 0) {
                habbo.getClient().sendResponse(new DanceMessageComposer(roomHabbo.getRoomUnit()));
            }

            if (roomHabbo.getRoomUnit().getHandItem() > 0) {
                habbo.getClient().sendResponse(new CarryObjectMessageComposer(roomHabbo.getRoomUnit()));
            }

            if (roomHabbo.getRoomUnit().getEffectId() > 0) {
                habbo.getClient().sendResponse(new AvatarEffectMessageComposer(roomHabbo.getRoomUnit()));
            }

            if (roomHabbo.getRoomUnit().isIdle()) {
                habbo.getClient().sendResponse(new SleepMessageComposer(roomHabbo.getRoomUnit()));
            }

            if (roomHabbo.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) {
                roomHabbo.getClient().sendResponse(new IgnoreResultMessageComposer(habbo, IgnoreResultMessageComposer.IGNORED));
            }

            if (!roomHabbo.getHabboStats().allowTalk()) {
                habbo.getClient().sendResponse(new IgnoreResultMessageComposer(roomHabbo, IgnoreResultMessageComposer.MUTED));
            } else if (habbo.getHabboStats().userIgnored(roomHabbo.getHabboInfo().getId())) {
                habbo.getClient().sendResponse(new IgnoreResultMessageComposer(roomHabbo, IgnoreResultMessageComposer.IGNORED));
            }

            if (roomHabbo.getHabboStats().getGuild() != 0 && !guildBadges.containsKey(roomHabbo.getHabboStats().getGuild())) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(roomHabbo.getHabboStats().getGuild());

                if (guild != null) {
                    guildBadges.put(roomHabbo.getHabboStats().getGuild(), guild.getBadge());
                }
            }

            if (roomHabbo.getRoomUnit().getRoomUnitType().equals(RoomUnitType.PET)) {
                try {
                    habbo.getClient().sendResponse(new UserRemoveMessageComposer(roomHabbo.getRoomUnit()));
                    habbo.getClient().sendResponse(new RoomUserPetComposer(((PetData) roomHabbo.getHabboStats().getCache().get("pet_type")).getType(), (Integer) roomHabbo.getHabboStats().getCache().get("pet_race"), (String) roomHabbo.getHabboStats().getCache().get("pet_color"), roomHabbo));
                } catch (Exception ignored) {

                }
            }
        }

        habbo.getClient().sendResponse(new HabboGroupBadgesMessageComposer(guildBadges));

        if ((room.hasRights(habbo)
                || (room.hasGuild()
                && room.getGuildRightLevel(habbo).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS)))
                && !room.getHabboQueue().isEmpty()) {
            for (Habbo waiting : room.getHabboQueue().valueCollection()) {
                habbo.getClient().sendResponse(new DoorbellMessageComposer(waiting.getHabboInfo().getUsername()));
            }
        }

        if (room.getPollId() > 0 && !PollManager.donePoll(habbo.getClient().getHabbo(), room.getPollId())) {
            Poll poll = Emulator.getGameEnvironment().getPollManager().getPoll(room.getPollId());

            if (poll != null) {
                habbo.getClient().sendResponse(new PollOfferComposer(poll));
            }
        }

        if (room.hasActiveWordQuiz()) {
            habbo.getClient().sendResponse(new QuestionComposer((Emulator.getIntUnixTimestamp() - room.getWordQuizEnd()) * 1000, room.getWordQuiz()));

            if (room.hasVotedInWordQuiz(habbo)) {
                habbo.getClient().sendResponse(new QuestionFinishedComposer(room.getNoVotes(), room.getYesVotes()));
            }
        }

        WiredHandler.handle(WiredTriggerType.ENTER_ROOM, habbo.getRoomUnit(), room, null);
        room.habboEntered(habbo);

        if (!habbo.getHabboStats().isNux() && (room.isOwner(habbo) || room.isPublicRoom())) {
            NewUserExperienceScriptProceedEvent.handle(habbo);
        }
    }

    void logEnter(Habbo habbo, Room room) {
        habbo.getHabboStats().roomEnterTimestamp = Emulator.getIntUnixTimestamp();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_enter_log (room_id, user_id, timestamp) VALUES(?, ?, ?)")) {
            statement.setInt(1, room.getId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, (int) (habbo.getHabboStats().getRoomEnterTimestamp()));
            statement.execute();

            if (!habbo.getHabboStats().visitedRoom(room.getId()))
                habbo.getHabboStats().addVisitRoom(room.getId());
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public void leaveRoom(Habbo habbo, Room room) {
        this.leaveRoom(habbo, room, true);
    }

    public void leaveRoom(Habbo habbo, Room room, boolean redirectToHotelView) {
        if (habbo.getHabboInfo().getCurrentRoom() != null && habbo.getHabboInfo().getCurrentRoom() == room) {
            habbo.getRoomUnit().setPathFinderRoom(null);

            this.logExit(habbo);
            room.removeHabbo(habbo, true);

            if (redirectToHotelView) {
                habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
            }
            habbo.getHabboInfo().setCurrentRoom(null);
            habbo.getRoomUnit().setKicked(false);

            if (room.getOwnerId() != habbo.getHabboInfo().getId()) {
                AchievementManager.progressAchievement(room.getOwnerId(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHosting"), (int) Math.floor((Emulator.getIntUnixTimestamp() - habbo.getHabboStats().roomEnterTimestamp) / 60000.0));
            }

            habbo.getMessenger().connectionChanged(habbo, habbo.isOnline(), false);
        }
    }

    public void logExit(Habbo habbo) {
        Emulator.getPluginManager().fireEvent(new UserExitRoomEvent(habbo, UserExitRoomEvent.UserExitRoomReason.DOOR));
        if (habbo.getRoomUnit().getCacheable().containsKey("control")) {
            Habbo control = (Habbo) habbo.getRoomUnit().getCacheable().remove("control");
            control.getRoomUnit().getCacheable().remove("controller");
        }

        if (habbo.getHabboInfo().getRiding() != null) {
            if (habbo.getHabboInfo().getRiding().getRoomUnit() != null) {
                habbo.getHabboInfo().getRiding().getRoomUnit().setGoalLocation(habbo.getHabboInfo().getRiding().getRoomUnit().getCurrentLocation());
            }
            habbo.getHabboInfo().getRiding().setTask(PetTasks.FREE);
            habbo.getHabboInfo().getRiding().setRider(null);
            habbo.getHabboInfo().setRiding(null);
        }

        Room room = habbo.getHabboInfo().getCurrentRoom();
        if (room != null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE room_enter_log SET exit_timestamp = ? WHERE user_id = ? AND room_id = ? ORDER BY timestamp DESC LIMIT 1")) {
                statement.setInt(1, Emulator.getIntUnixTimestamp());
                statement.setInt(2, habbo.getHabboInfo().getId());
                statement.setInt(3, room.getId());
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    public Set<String> getTags() {
        Map<String, Integer> tagCount = new HashMap<>();

        for (Room room : this.activeRooms.values()) {
            for (String s : room.getTags().split(";")) {
                int i = 0;
                if (tagCount.get(s) != null)
                    i++;

                tagCount.put(s, i++);
            }
        }
        return new TreeMap<>(tagCount).keySet();
    }

    public ArrayList<Room> getPublicRooms() {
        return this.activeRooms.values().stream().filter(Room::isPublicRoom).sorted(Room.SORT_ID) .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Room> getPopularRooms(int count) {
        return this.activeRooms.values().stream()
                .filter(room -> room.getUserCount() > 0 && (!room.isPublicRoom() || RoomManager.SHOW_PUBLIC_IN_POPULAR_TAB))
                .sorted()
                .limit(count)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Room> getPopularRooms(int count, int category) {
        return this.activeRooms.values().stream()
                .filter(room -> !room.isPublicRoom() && room.getCategory() == category)
                .sorted()
                .limit(count)
                .toList();
    }

    public Map<Integer, List<Room>> getPopularRoomsByCategory(int count) {
        Map<Integer, List<Room>> rooms = new HashMap<>();

        for (Room room : this.activeRooms.values()) {
            if (!room.isPublicRoom()) {
                if (!rooms.containsKey(room.getCategory())) {
                    rooms.put(room.getCategory(), new ArrayList<>());
                }

                rooms.get(room.getCategory()).add(room);
            }
        }

        Map<Integer, List<Room>> result = new HashMap<>();

        for (Map.Entry<Integer, List<Room>> set : rooms.entrySet()) {
            if (set.getValue().isEmpty())
                continue;

            Collections.sort(set.getValue());

            result.put(set.getKey(), new ArrayList<>(set.getValue().subList(0, (Math.min(set.getValue().size(), count)))));
        }

        return result;
    }

    public List<Room> getRoomsWithName(String name) {
        List<Room> rooms = new ArrayList<>(activeRooms.values().stream().filter(room -> room.getName().equalsIgnoreCase(name)).toList());

        if (rooms.size() < 25) {
            rooms.addAll(this.getOfflineRoomsWithName(name));
        }

        Collections.sort(rooms);

        return rooms;
    }

    private ArrayList<Room> getOfflineRoomsWithName(String name) {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, rooms.* FROM rooms INNER JOIN users ON owner_id = users.id WHERE name LIKE ? ORDER BY id DESC LIMIT 25")) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                        continue;

                    Room r = new Room(set);
                    rooms.add(r);
                    this.activeRooms.put(r.getId(), r);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public List<Room> getRoomsWithTag(String tag) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (Room room : this.activeRooms.values()) {
            for (String s : room.getTags().split(";")) {
                if (s.equalsIgnoreCase(tag)) {
                    rooms.add(room);
                    break;
                }
            }
        }

        Collections.sort(rooms);

        return rooms;
    }

    public List<Room> getGroupRoomsWithName(String name) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (Room room : this.activeRooms.values()) {
            if (room.getGuildId() == 0)
                continue;

            if (room.getName().toLowerCase().contains(name.toLowerCase()))
                rooms.add(room);
        }

        if (rooms.size() < 25) {
            rooms.addAll(this.getOfflineGroupRoomsWithName(name));
        }

        Collections.sort(rooms);

        return rooms;
    }

    private ArrayList<Room> getOfflineGroupRoomsWithName(String name) {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, rooms.* FROM rooms INNER JOIN users ON rooms.owner_id = users.id WHERE name LIKE ? AND guild_id != 0 ORDER BY id DESC LIMIT 25")) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                        continue;

                    Room r = new Room(set);
                    rooms.add(r);

                    this.activeRooms.put(r.getId(), r);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public List<Room> getRoomsFriendsNow(Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (MessengerBuddy buddy : habbo.getMessenger().getFriends().values()) {
            if (buddy.getOnline() == 0)
                continue;

            Habbo friend = Emulator.getGameEnvironment().getHabboManager().getHabbo(buddy.getId());
            if (friend == null || friend.getHabboInfo().getCurrentRoom() == null)
                continue;

            rooms.add(friend.getHabboInfo().getCurrentRoom());
        }

        Collections.sort(rooms);

        return rooms;
    }

    public List<Room> getRoomsFriendsOwn(Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (MessengerBuddy buddy : habbo.getMessenger().getFriends().values()) {
            if (buddy.getOnline() == 0)
                continue;

            Habbo friend = Emulator.getGameEnvironment().getHabboManager().getHabbo(buddy.getId());

            if (friend == null)
                continue;

            rooms.addAll(this.getRoomsForHabbo(friend));
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getRoomsVisited(Habbo habbo, boolean includeSelf, int limit) {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM room_enter_log INNER JOIN rooms ON room_enter_log.room_id = rooms.id WHERE user_id = ? AND timestamp >= ? AND rooms.owner_id != ? GROUP BY rooms.id AND timestamp ORDER BY timestamp DESC LIMIT " + limit)) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, Emulator.getIntUnixTimestamp() - 259200);
            statement.setInt(3, (includeSelf ? 0 : habbo.getHabboInfo().getId()));
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Room room = this.activeRooms.get(set.getInt("id"));

                    if (room == null) {
                        room = new Room(set);

                        this.activeRooms.put(room.getId(), room);
                    }

                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getRoomsFavourite(Habbo habbo) {
        final ArrayList<Room> rooms = new ArrayList<>();

        habbo.getHabboStats().getFavoriteRooms().forEach(value -> {
            Room room = RoomManager.this.getRoom(value);

            if (room != null) {
                if (room.getState() == RoomState.INVISIBLE) {
                    room.loadData();
                    if (!room.hasRights(habbo)) return true;
                }
                rooms.add(room);
            }
            return true;
        });

        return rooms;
    }

    public List<Room> getGroupRooms(Habbo habbo, int limit) {
        final ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM rooms INNER JOIN guilds_members ON guilds_members.guild_id = rooms.guild_id WHERE guilds_members.user_id = ? AND level_id != 3")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id"))) {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    } else {
                        rooms.add(new Room(set));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        Collections.sort(rooms);

        return rooms.subList(0, (Math.min(rooms.size(), limit)));
    }

    public List<Room> getRoomsWithRights(Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM rooms INNER JOIN room_rights ON room_rights.room_id = rooms.id WHERE room_rights.user_id = ? ORDER BY rooms.id DESC LIMIT 30")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id"))) {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    } else {
                        rooms.add(new Room(set));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public ArrayList<Room> getRoomsWithFriendsIn(Habbo habbo, int limit) {
        final ArrayList<Room> rooms = new ArrayList<>();

        for (MessengerBuddy buddy : habbo.getMessenger().getFriends().values()) {
            Habbo friend = Emulator.getGameEnvironment().getHabboManager().getHabbo(buddy.getId());

            if (friend == null || friend.getHabboInfo() == null) continue;

            Room room = friend.getHabboInfo().getCurrentRoom();
            if (room != null && !rooms.contains(room) && room.hasRights(habbo)) rooms.add(room);

            if (rooms.size() >= limit) break;
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getTopRatedRooms(int limit) {
        final ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms ORDER BY score DESC LIMIT ?")) {
            statement.setInt(1, limit);

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id"))) {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    } else {
                        rooms.add(new Room(set));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public List<Room> getRoomsWithAdminRights(Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms INNER JOIN guilds_members ON guilds_members.guild_id = rooms.guild_id WHERE guilds_members.user_id = ? AND level_id = 0")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.activeRooms.containsKey(set.getInt("id"))) {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    } else {
                        rooms.add(new Room(set));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return rooms;
    }

    public List<Room> getRoomsInGroup() {
        return new ArrayList<>();
    }

    public ArrayList<Room> getRoomsPromoted() {
        return activeRooms.values().stream().filter(Room::isPromoted).collect(Collectors.toCollection(ArrayList::new));    }

    public ArrayList<Room> getRoomsStaffPromoted() {
        return activeRooms.values().stream().filter(Room::isStaffPromotedRoom).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Room> filterRoomsByOwner(List<Room> rooms, String filter) {
        return rooms.stream().filter(r -> r.getOwnerName().equalsIgnoreCase(filter)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Room> filterRoomsByName(List<Room> rooms, String filter) {
        return rooms.stream().filter(room -> room.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Room> filterRoomsByNameAndDescription(List<Room> rooms, String filter) {
        return rooms.stream().filter(room -> room.getName().toLowerCase().contains(filter.toLowerCase()) || room.getDescription().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Room> filterRoomsByTag(List<Room> rooms, String filter) {
        ArrayList<Room> r = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getTags().split(";").length == 0)
                continue;

            for (String s : room.getTags().split(";")) {
                if (s.equalsIgnoreCase(filter))
                    r.add(room);
            }
        }

        return r;
    }

    public ArrayList<Room> filterRoomsByGroup(List<Room> rooms, String filter) {
        return rooms.stream().filter(room -> room.getGuildId() != 0)
                .filter(room -> Emulator.getGameEnvironment().getGuildManager().getGuild(room.getGuildId()).getName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public synchronized void dispose() {
        for (Room room : this.activeRooms.values()) {
            room.dispose();
        }

        this.activeRooms.clear();

        log.info("Room Manager -> Disposed!");
    }

    public CustomRoomLayout insertCustomLayout(Room room, String map, int doorX, int doorY, int doorDirection) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_models_custom (id, name, door_x, door_y, door_dir, heightmap) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE door_x = ?, door_y = ?, door_dir = ?, heightmap = ?")) {
            statement.setInt(1, room.getId());
            statement.setString(2, "custom_" + room.getId());
            statement.setInt(3, doorX);
            statement.setInt(4, doorY);
            statement.setInt(5, doorDirection);
            statement.setString(6, map);
            statement.setInt(7, doorX);
            statement.setInt(8, doorY);
            statement.setInt(9, doorDirection);
            statement.setString(10, map);
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        return this.loadCustomLayout(room);
    }

    public void banUserFromRoom(Habbo rights, int userId, int roomId, RoomBanTypes length) {
        Room room = this.getRoom(roomId);

        if (room == null)
            return;

        if (rights != null && !room.hasRights(rights))
            return;

        String name = "";

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        if (habbo != null) {
            if (habbo.hasRight(Permission.ACC_UNKICKABLE)) {
                return;
            }

            name = habbo.getHabboInfo().getUsername();
        } else {
            HabboInfo info = HabboManager.getOfflineHabboInfo(userId);

            if (info != null) {
                if (info.getPermissionGroup().hasRight(Permission.ACC_UNKICKABLE, false)) {
                    return;
                }
                name = info.getUsername();
            }
        }

        if (name.isEmpty()) {
            return;
        }

        RoomBan roomBan = new RoomBan(roomId, userId, name, Emulator.getIntUnixTimestamp() + length.duration);
        roomBan.insert();

        room.addRoomBan(roomBan);

        if (habbo != null && habbo.getHabboInfo().getCurrentRoom() == room) {
            room.removeHabbo(habbo, true);
            habbo.getClient().sendResponse(new CantConnectMessageComposer(CantConnectMessageComposer.ROOM_ERROR_BANNED));
        }
    }

    public void registerGameType(Class<? extends Game> gameClass) {
        gameTypes.add(gameClass);
    }

    public void unregisterGameType(Class<? extends Game> gameClass) {
        gameTypes.remove(gameClass);
    }

    @Getter
    @AllArgsConstructor
    public enum RoomBanTypes {
        RWUAM_BAN_USER_HOUR(60 * 60),
        RWUAM_BAN_USER_DAY(24 * 60 * 60),
        RWUAM_BAN_USER_PERM(10 * 365 * 24 * 60 * 60);

        private final int duration;
    }
}
