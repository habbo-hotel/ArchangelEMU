package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class RoomWiredManager {
    private final Room room;
    private final ConcurrentHashMap<Integer, InteractionWired> currentWireds;
    private final ConcurrentHashMap<WiredTriggerType, Set<InteractionWiredTrigger>> currentWiredTriggers;
    private final ConcurrentHashMap<WiredEffectType, Set<InteractionWiredEffect>> currentWiredEffects;
    private final ConcurrentHashMap<WiredConditionType, Set<InteractionWiredCondition>> currentWiredConditions;
    private final ConcurrentHashMap<Integer, InteractionWiredExtra> currentWiredExtras;

    public RoomWiredManager(Room room) {
        this.room = room;
        this.currentWireds = new ConcurrentHashMap<>(0);
        this.currentWiredTriggers = new ConcurrentHashMap<>(0);
        this.currentWiredEffects = new ConcurrentHashMap<>(0);
        this.currentWiredConditions = new ConcurrentHashMap<>(0);
        this.currentWiredExtras = new ConcurrentHashMap<>(0);
    }

    public void addWired(InteractionWired wired) {
        this.currentWireds.put(wired.getId(), wired);
        sortWired(wired);
    }

    public void removeWired(InteractionWired wired) {
        InteractionWired w = this.currentWireds.remove(wired.getId());

        if (w instanceof InteractionWiredTrigger trigger) {
            removeWiredTrigger(trigger);
        } else if (w instanceof InteractionWiredEffect effect) {
            removeWiredEffect(effect);
        } else if (w instanceof InteractionWiredCondition condition) {
            removeWiredCondition(condition);
        } else if (w instanceof InteractionWiredExtra extra) {
            removeWiredExtra(extra);
        } else {
            log.error("Error occurred while removing undefined Wired Type");
        }
    }

    private void sortWired(InteractionWired wired) {
        if (wired instanceof InteractionWiredTrigger trigger) {
            addWiredTrigger(trigger);
        } else if (wired instanceof InteractionWiredEffect effect) {
            addWiredEffect(effect);
        } else if (wired instanceof InteractionWiredCondition condition) {
            addWiredCondition(condition);
        } else if (wired instanceof InteractionWiredExtra extra) {
            addWiredExtra(extra);
        } else {
            log.error("Undefined Wired Type");
        }
    }

    public void addWiredTrigger(InteractionWiredTrigger trigger) {
        currentWiredTriggers.computeIfAbsent(trigger.getType(), k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(trigger);
    }

    public void removeWiredTrigger(InteractionWiredTrigger trigger) {
        Set<InteractionWiredTrigger> triggers = currentWiredTriggers.get(trigger.getType());
        if (triggers != null) {
            triggers.remove(trigger);
            if (triggers.isEmpty()) {
                currentWiredTriggers.remove(trigger.getType());
            }
        }
    }

    public void addWiredEffect(InteractionWiredEffect effect) {
        currentWiredEffects.computeIfAbsent(effect.getType(), k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(effect);
    }

    public void removeWiredEffect(InteractionWiredEffect effect) {
        Set<InteractionWiredEffect> effects = currentWiredEffects.get(effect.getType());
        if (effects != null) {
            effects.remove(effect);
            if (effects.isEmpty()) {
                currentWiredEffects.remove(effect.getType());
            }
        }
    }

    public void addWiredCondition(InteractionWiredCondition condition) {
        currentWiredConditions.computeIfAbsent(condition.getType(), k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(condition);
    }

    public void removeWiredCondition(InteractionWiredCondition condition) {
        Set<InteractionWiredCondition> conditions = currentWiredConditions.get(condition.getType());
        if (conditions != null) {
            conditions.remove(condition);
            if (conditions.isEmpty()) {
                currentWiredConditions.remove(condition.getType());
            }
        }
    }

    public void addWiredExtra(InteractionWiredExtra extra) {
        currentWiredExtras.put(extra.getId(), extra);
    }

    public void removeWiredExtra(InteractionWiredExtra extra) {
        currentWiredExtras.remove(extra.getId());
    }

    public void setHideWired(boolean hideWired) {
        //TODO FIX THIS
//        this.room.getRoomInfo().setHiddenWiredEnabled(hideWired);
//
//        if (this.room.getRoomInfo().isHiddenWiredEnabled()) {
//            for (RoomItem item : this.currentWireds.values()) {
//                this.room.sendComposer(new RemoveFloorItemComposer(item).compose());
//            }
//        } else {
//            this.room.sendComposer(new ObjectsMessageComposer(this.room.getFurniOwnerNames(), this.currentWireds).compose());
//        }
    }

    public void clear() {
        this.currentWireds.clear();
        this.currentWiredTriggers.clear();
        this.currentWiredConditions.clear();
        this.currentWiredEffects.clear();
        this.currentWiredExtras.clear();
    }
}
