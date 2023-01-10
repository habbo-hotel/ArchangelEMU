package com.eu.habbo.habbohotel.games.tag;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public abstract class TagGame extends Game {
    protected final THashMap<Habbo, InteractionTagPole> taggers = new THashMap<>();

    public TagGame(Class<? extends GameTeam> gameTeamClazz, Class<? extends GamePlayer> gamePlayerClazz, Room room) {
        super(gameTeamClazz, gamePlayerClazz, room, false);
    }

    @EventHandler
    public static void onUserLookAtPoint(RoomUnitLookAtPointEvent event) {
        if (event.room == null || event.roomUnit == null || event.location == null) return;

        if (RoomLayout.tilesAdjecent(event.roomUnit.getCurrentLocation(), event.location)) {
            Habbo habbo = event.room.getHabbo(event.roomUnit);

            if (habbo != null) {
                if (habbo.getHabboInfo().getCurrentGame() != null) {
                    if (TagGame.class.isAssignableFrom(habbo.getHabboInfo().getCurrentGame())) {
                        TagGame game = (TagGame) event.room.getGame(habbo.getHabboInfo().getCurrentGame());

                        if (game != null) {
                            if (game.isTagger(habbo)) {
                                for (Habbo tagged : event.room.getHabbosAt(event.location)) {
                                    if (tagged == habbo || tagged.getHabboInfo().getCurrentGame() == null || tagged.getHabboInfo().getCurrentGame() != habbo.getHabboInfo().getCurrentGame()) {
                                        continue;
                                    }

                                    game.tagged(event.room, habbo, tagged);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onUserWalkEvent(UserTakeStepEvent event) {
        if (event.habbo.getHabboInfo().getCurrentGame() != null && TagGame.class.isAssignableFrom(event.habbo.getHabboInfo().getCurrentGame())) {
            THashSet<HabboItem> items = event.habbo.getHabboInfo().getCurrentRoom().getItemsAt(event.toLocation);

            TagGame game = (TagGame) event.habbo.getHabboInfo().getCurrentRoom().getGame(event.habbo.getHabboInfo().getCurrentGame());

            if (game != null) {
                for (HabboItem item : items) {
                    if (item instanceof InteractionTagField && ((InteractionTagField) item).gameClazz == event.habbo.getHabboInfo().getCurrentGame()) {
                        if (game.taggers.isEmpty()) {
                            game.tagged(event.habbo.getHabboInfo().getCurrentRoom(), null, event.habbo);
                        }
                        return;
                    }
                }

                game.removeHabbo(event.habbo);
            }
        }
    }

    public abstract Class<? extends InteractionTagPole> getTagPole();

    public abstract int getMaleEffect();

    public abstract int getMaleTaggerEffect();

    public abstract int getFemaleEffect();

    public abstract int getFemaleTaggerEffect();

    public void tagged(Room room, Habbo tagger, Habbo tagged) {
        if (this.taggers.containsKey(tagged)) {
            return;
        }

        THashSet<HabboItem> poles = room.getRoomSpecialTypes().getItemsOfType(this.getTagPole());
        InteractionTagPole pole = this.taggers.get(tagger);
        room.giveEffect(tagged, this.getTaggedEffect(tagged), -1);

        if (poles.size() > this.taggers.size()) {
            for (Map.Entry<Habbo, InteractionTagPole> set : this.taggers.entrySet()) {
                poles.remove(set.getValue());
            }

            for (HabboItem item : poles) {
                tagged.getHabboInfo().getCurrentRoom().giveEffect(tagged, this.getTaggedEffect(tagged), -1);
                this.taggers.put(tagged, (InteractionTagPole) item);
            }
        } else {
            if (tagger != null) {
                room.giveEffect(tagger, this.getEffect(tagger), -1);
                this.taggers.remove(tagger);
            }

            this.taggers.put(tagged, pole);
        }

        if (pole != null) {
            pole.setExtradata("1");
            room.updateItemState(pole);
            Emulator.getThreading().run(new HabboItemNewState(pole, room, "0"), 1000);
        }
    }

    @Override
    public synchronized boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        super.addHabbo(habbo, GameTeamColors.RED);

        RoomUnit roomUnit = habbo.getRoomUnit();
        if (this.getTagPole() != null) {
            THashSet<HabboItem> poles = habbo.getHabboInfo().getCurrentRoom().getRoomSpecialTypes().getItemsOfType(this.getTagPole());

            if (poles.size() > this.taggers.size()) {
                for (Map.Entry<Habbo, InteractionTagPole> set : this.taggers.entrySet()) {
                    poles.remove(set.getValue());
                }

                TObjectHashIterator<HabboItem> iterator = poles.iterator();
                if ((iterator.hasNext())) {
                    HabboItem item = iterator.next();
                    if (roomUnit.getEffectId() > 0)
                        roomUnit.setPreviousEffectId(roomUnit.getEffectId(), roomUnit.getPreviousEffectEndTimestamp());
                    habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, this.getEffect(habbo), -1, true);
                    this.room.scheduledTasks.add(() -> habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, this.getTaggedEffect(habbo), -1, true));
                    this.taggers.put(habbo, (InteractionTagPole) item);
                    return true;
                }
            }
        } else {
            if (this.taggers.isEmpty()) {
                if (roomUnit.getEffectId() > 0)
                    roomUnit.setPreviousEffectId(roomUnit.getEffectId(), roomUnit.getPreviousEffectEndTimestamp());
                habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, this.getEffect(habbo), -1, true);
                this.room.scheduledTasks.add(() -> habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, this.getTaggedEffect(habbo), -1, true));
                this.taggers.put(habbo, null);
                return true;
            }
        }
        if (roomUnit.getEffectId() > 0)
            roomUnit.setPreviousEffectId(roomUnit.getEffectId(), roomUnit.getPreviousEffectEndTimestamp());
        habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, this.getEffect(habbo), -1, true);

        return true;
    }

    @Override
    public synchronized void removeHabbo(Habbo habbo) {
        super.removeHabbo(habbo);
        this.taggers.remove(habbo);

        RoomUnit roomUnit = habbo.getRoomUnit();
        Room room = roomUnit.getRoom();
        if (room == null) return;

        HabboItem topItem = room.getTopItemAt(roomUnit.getCurrentLocation().getX(), roomUnit.getCurrentLocation().getY());
        int nextEffectM = 0;
        int nextEffectF = 0;
        int nextEffectDuration = -1;

        if (topItem != null) {
            nextEffectM = topItem.getBaseItem().getEffectM();
            nextEffectF = topItem.getBaseItem().getEffectF();
        } else if (roomUnit.getPreviousEffectId() > 0) {
            nextEffectF = roomUnit.getPreviousEffectId();
            nextEffectM = roomUnit.getPreviousEffectId();
            nextEffectDuration = roomUnit.getPreviousEffectEndTimestamp();
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
            room.giveEffect(habbo, nextEffectM, nextEffectDuration, true);
            return;
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.F)) {
            room.giveEffect(habbo, nextEffectF, nextEffectDuration, true);
        }
    }

    @Override
    public void initialise() {

    }

    @Override
    public void run() {

    }

    public int getEffect(Habbo habbo) {
        if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
            return this.getMaleEffect();
        }

        return this.getFemaleEffect();
    }

    public int getTaggedEffect(Habbo habbo) {
        if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
            return this.getMaleTaggerEffect();
        }

        return this.getFemaleTaggerEffect();
    }

    public boolean isTagger(Habbo habbo) {
        return this.taggers.containsKey(habbo);
    }
}