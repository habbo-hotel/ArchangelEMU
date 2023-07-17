package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetToy;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.items.types.RoomFloorItem;
import com.eu.habbo.habbohotel.rooms.entities.items.types.RoomWallItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoomItemManager {
    private final ConcurrentHashMap<Integer, RoomItem> currentItems;
    private final HashMap<Integer, RoomFloorItem> floorItems;
    private final HashMap<Integer, RoomWallItem> wallItems;
    private final HashSet<ICycleable> cycleTasks;
    private final HashMap<WiredTriggerType, List<InteractionWiredTrigger>> wiredTriggers;
    private final HashMap<WiredEffectType, List<InteractionWiredEffect>> wiredEffects;
    private final HashMap<WiredConditionType, List<InteractionWiredCondition>> wiredConditions;
    private final HashMap<Integer, InteractionWiredExtra> wiredExtras;
    private final HashMap<Integer, InteractionNest> nests;
    private final HashMap<Integer, InteractionPetDrink> petDrinks;
    private final HashMap<Integer, InteractionPetFood> petFoods;
    private final HashMap<Integer, InteractionPetToy> petToys;
    private final HashMap<Integer, InteractionRoller> rollers;
    private final HashMap<Integer, InteractionGameScoreboard> gameScoreboards;
    private final HashMap<Integer, InteractionGameGate> gameGates;
    private final HashMap<Integer, InteractionGameTimer> gameTimers;
    private final HashMap<Integer, InteractionBattleBanzaiTeleporter> banzaiTeleporters;
    private final HashMap<Integer, InteractionFreezeExitTile> freezeExitTile;
    public RoomItemManager() {
        this.currentItems = new ConcurrentHashMap<>();

        this.floorItems = new HashMap<>();
        this.wallItems = new HashMap<>();

        this.cycleTasks = new HashSet<>(0);

        this.wiredTriggers = new HashMap<>(0);
        this.wiredEffects = new HashMap<>(0);
        this.wiredConditions = new HashMap<>(0);
        this.wiredExtras = new HashMap<>(0);

        this.nests = new HashMap<>(0);
        this.petDrinks = new HashMap<>(0);
        this.petFoods = new HashMap<>(0);
        this.petToys = new HashMap<>(0);

        this.rollers = new HashMap<>(0);

        this.gameScoreboards = new HashMap<>(0);
        this.gameGates = new HashMap<>(0);
        this.gameTimers = new HashMap<>(0);

        this.banzaiTeleporters = new HashMap<>(0);
        this.freezeExitTile = new HashMap<>(0);
    }
}
