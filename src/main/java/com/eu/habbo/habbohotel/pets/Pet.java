package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetTree;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.PetLevelUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetExperienceComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetRespectNotificationComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;
import com.eu.habbo.plugin.events.pets.PetTalkEvent;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * This class represents a virtual pet in a virtual world. It has fields representing the state of the
 * pet, such as its name, type, level, hunger level, and thirst level. It also has methods for
 * interacting with the virtual world, such as speaking, updating its status, and running tasks.
 */
public class Pet implements ISerialize, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pet.class);

    /**
     * The level of thirst of the pet.
     */
    public int levelThirst;

    /**
     * The level of hunger of the pet.
     */
    public int levelHunger;

    /**
     * Whether the pet needs to be updated.
     */
    public boolean needsUpdate = false;

    /**
     * Whether the pet needs to be sent as a packet update.
     */
    public boolean packetUpdate = false;

    /**
     * The ID of the pet.
     */
    protected int id;

    /**
     * The ID of the user that owns the pet.
     */
    protected int userId;

    /**
     * The room that the pet is in.
     */
    protected Room room;

    /**
     * The name of the pet.
     */
    protected String name;

    /**
     * The data for the type of pet.
     */
    protected PetData petData;

    /**
     * The race of the pet.
     */
    protected int race;

    /**
     * The color of the pet.
     */
    protected String color;

    /**
     * The happyness level of the pet.
     */
    protected int happyness;

    /**
     * The experience points of the pet.
     */
    protected int experience;

    /**
     * The energy level of the pet.
     */
    protected int energy;

    /**
     * The respect points of the pet.
     */
    protected int respect;

    /**
     * The timestamp of when the pet was created.
     */
    protected int created;

    /**
     * The current level of the pet.
     */
    protected int level;

    /**
     * The unit that represents the pet in a room.
     */
    RoomUnit roomUnit;

    /**
     * The chat timeout of the pet.
     */
    private int chatTimeout;

    /**
     * The tick timeout of the pet.
     */
    private int tickTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The happyness delay of the pet.
     */
    private int happynessDelay = Emulator.getIntUnixTimestamp();

    /**
     * The gesture tick timeout of the pet.
     */
    private int gestureTickTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The random action tick timeout of the pet.
     */
    private int randomActionTickTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The posture timeout of the pet.
     */
    private int postureTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The time when the pet started staying.
     */
    private int stayStartedAt = 0;
    /**
     * The number of ticks that the pet has spent idle while waiting for a command.
     */
    private int idleCommandTicks = 0;

    /**
     * The number of ticks that the pet has spent free after completing a command.
     */
    private int freeCommandTicks = -1;

    /**
     * The current task of the pet.
     */
    private PetTasks task = PetTasks.FREE;

    /**
     * Whether the pet is muted.
     */
    private boolean muted = false;

    /**
     * Creates a new pet using the given result set, which should contain data retrieved from a
     * database.
     *
     * @param set the result set containing the pet data
     * @throws SQLException if an error occurs while reading from the result set
     */
    public Pet(ResultSet set) throws SQLException {
        super();
        this.id = set.getInt("id");
        this.userId = set.getInt("user_id");
        this.room = null;
        this.name = set.getString("name");
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(set.getInt("type"));
        if (this.petData == null) {
            LOGGER.error("WARNING! Missing pet data for type: " + set.getInt("type") + "! Insert a new entry into the pet_actions table for this type!");
            this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(0);
        }
        this.race = set.getInt("race");
        this.experience = set.getInt("experience");
        this.happyness = set.getInt("happyness");
        this.energy = set.getInt("energy");
        this.respect = set.getInt("respect");
        this.created = set.getInt("created");
        this.color = set.getString("color");
        this.levelThirst = set.getInt("thirst");
        this.levelHunger = set.getInt("hunger");
        this.level = PetManager.getLevel(this.experience);
    }

    /**
     * Creates a new pet with the given type, race, color, name, and owner.
     *
     * @param type the type of the pet
     * @param race the race of the pet
     * @param color the color of the pet
     * @param name the name of the pet
     * @param userId the ID of the user that owns the pet
     */
    public Pet(int type, int race, String color, String name, int userId) {
        this.id = 0;
        this.userId = userId;
        this.room = null;
        this.name = name;
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(type);

        if (this.petData == null) {
            LOGGER.warn("Missing pet data for type: " + type + "! Insert a new entry into the pet_actions table for this type!");
        }

        this.race = race;
        this.color = color;
        this.experience = 0;
        this.happyness = 100;
        this.energy = 100;
        this.respect = 0;
        this.levelThirst = 0;
        this.levelHunger = 0;
        this.created = Emulator.getIntUnixTimestamp();
        this.level = 1;
    }

    /**
     * Makes the pet say the given message in the room it is currently in.
     *
     * @param message the message to be said
     */
    protected void say(String message) {
        if (this.roomUnit != null && this.room != null && !message.isEmpty()) {
            RoomChatMessage chatMessage = new RoomChatMessage(message, this.roomUnit, RoomChatMessageBubbles.NORMAL);
            PetTalkEvent talkEvent = new PetTalkEvent(this, chatMessage);
            if (!Emulator.getPluginManager().fireEvent(talkEvent).isCancelled()) {
                this.room.petChat(new ChatMessageComposer(chatMessage).compose());
            }
        }
    }


    public void say(PetVocal vocal) {
        if (vocal != null)
            this.say(vocal.message);
    }


    public void addEnergy(int amount) {
        this.energy += amount;

        /* this is regeneration, add back if needed, deleted when other stuff done
        if (this.energy > PetManager.maxEnergy(this.level))
            this.energy = PetManager.maxEnergy(this.level);
        */

        // never negative energy
        if (this.energy < 0)
            this.energy = 0;
    }

    /**
     * Increases the happyness of the pet by the given amount.
     *
     * @param amount the amount to increase the happyness by
     */
    public void addHappyness(int amount) {
        this.happyness += amount;

        if (this.happyness > 100)
            this.happyness = 100;

        if (this.happyness < 0)
            this.happyness = 0;
    }

    /**
     * Gets the respect of the pet.
     *
     * @return the respect of the pet
     */
    public int getRespect() {
        return this.respect;
    }

    /**
     * Increases the respect of the pet by 1.
     */
    public void addRespect() {
        this.respect++;
    }

    /**
     * Gets the number of days that the pet has been alive.
     *
     * @return the number of days that the pet has been alive
     */
    public int daysAlive() {
        return (Emulator.getIntUnixTimestamp() - this.created) / 86400;
    }

    /**
     * Gets the date that the pet was born as a string in the format "dd/MM/yyyy".
     *
     * @return the date that the pet was born as a string
     */
    public String bornDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(new java.util.Date(this.created));

        return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR);
    }

    /**
     * Updates the pet in the database with the current values. If the pet has not yet been saved to the database,
     * it will be inserted.
     */
    @Override
    public void run() {
        if (this.needsUpdate) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                if (this.id > 0) {
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE users_pets SET room_id = ?, experience = ?, energy = ?, respect = ?, x = ?, y = ?, z = ?, rot = ?, hunger = ?, thirst = ?, happyness = ?, created = ? WHERE id = ?")) {
                        statement.setInt(1, (this.room == null ? 0 : this.room.getId()));
                        statement.setInt(2, this.experience);
                        statement.setInt(3, this.energy);
                        statement.setInt(4, this.respect);
                        statement.setInt(5, this.roomUnit != null ? this.roomUnit.getX() : 0);
                        statement.setInt(6, this.roomUnit != null ? this.roomUnit.getY() : 0);
                        statement.setDouble(7, this.roomUnit != null ? this.roomUnit.getZ() : 0.0);
                        statement.setInt(8, this.roomUnit != null ? this.roomUnit.getBodyRotation().getValue() : 0);
                        statement.setInt(9, this.levelHunger);
                        statement.setInt(10, this.levelThirst);
                        statement.setInt(11, this.happyness);
                        statement.setInt(12, this.created);
                        statement.setInt(13, this.id);
                        statement.execute();
                    }
                } else if (this.id == 0) {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_pets (user_id, room_id, name, race, type, color, experience, energy, respect, created) VALUES (?, 0, ?, ?, ?, ?, 0, 0, 0, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        statement.setInt(1, this.userId);
                        statement.setString(2, this.name);
                        statement.setInt(3, this.race);
                        statement.setInt(4, 0);

                        if (this.petData != null) {
                            statement.setInt(4, this.petData.getType());
                        }

                        statement.setString(5, this.color);
                        statement.setInt(6, this.created);
                        statement.execute();

                        try (ResultSet set = statement.getGeneratedKeys()) {
                            if (set.next()) {
                                this.id = set.getInt(1);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }

            this.needsUpdate = false;
        }
    }

    /**
     *  Performs a cycle of updates for the pet. This includes updates to their walking, tasks, happiness, hunger, thirst, and energy levels.
     *  It also includes updates to their gestures and random actions, as well as vocalizing if they are not muted.
     */
    public void cycle() {
        this.idleCommandTicks++;

        int time = Emulator.getIntUnixTimestamp();
        if (this.roomUnit != null && this.task != PetTasks.RIDE) {
            if (time - this.gestureTickTimeout > 5 && this.roomUnit.hasStatus(RoomUnitStatus.GESTURE)) {
                this.roomUnit.removeStatus(RoomUnitStatus.GESTURE);
                this.packetUpdate = true;
            }

            if (time - this.postureTimeout > 1 && this.task == null) {
                this.clearPosture();
                this.postureTimeout = time + 120;
            }

            if (this.freeCommandTicks > 0) {
                this.freeCommandTicks--;

                if (this.freeCommandTicks == 0) {
                    this.freeCommand();
                }
            }

            if (!this.roomUnit.isWalking()) {
                if (this.roomUnit.getWalkTimeOut() < time && this.canWalk()) {
                    RoomTile tile = this.room.getRandomWalkableTile();

                    if (tile != null) {
                        this.roomUnit.setGoalLocation(tile);
                    }
                }

                if (this.task == PetTasks.NEST || this.task == PetTasks.DOWN) {
                    if (this.levelHunger > 0)
                        this.levelHunger--;

                    if (this.levelThirst > 0)
                        this.levelThirst--;

                   //this.addEnergy(5);

                    this.addHappyness(1);

                    if (this.energy == PetManager.maxEnergy(this.level)) {
                        this.roomUnit.removeStatus(RoomUnitStatus.LAY);
                        this.roomUnit.setCanWalk(true);
                        this.roomUnit.setGoalLocation(this.room.getRandomWalkableTile());
                        this.task = null;
                        this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.ENERGY.getKey());
                        this.gestureTickTimeout = time;
                    }
                } /* this is regeneration, add back if needed
                else if (this.tickTimeout >= 5) {
                    if (this.levelHunger < 100)
                        this.levelHunger++;

                    if (this.levelThirst < 100)
                        this.levelThirst++;

                    if (this.energy < PetManager.maxEnergy(this.level))
                        this.energy++;

                    this.tickTimeout = time;
                }
                */

                if (this.task == PetTasks.STAY && Emulator.getIntUnixTimestamp() - this.stayStartedAt >= 120) {
                    this.task = null;
                    this.getRoomUnit().setCanWalk(true);
                }
            } else {
                int timeout = Emulator.getRandom().nextInt(10) * 2;
                this.roomUnit.setWalkTimeOut(timeout < 20 ? 20 + time : timeout + time);

                if (this.energy >= 2)
                    this.addEnergy(-1);


                if (this.levelHunger < 100)
                    this.levelHunger++;

                if (this.levelThirst < 100)
                    this.levelThirst++;

                if (this.happyness > 0 && time - this.happynessDelay >= 30) {
                    this.happyness--;
                    this.happynessDelay = time;
                }
            }

            if (time - this.gestureTickTimeout > 15) {
                this.updateGesture(time);
            } else if (time - this.randomActionTickTimeout > 30) {
                this.randomAction();
                this.randomActionTickTimeout = time + (10 * Emulator.getRandom().nextInt(60));
            }

            if (!this.muted) {
                if (this.chatTimeout <= time) {
                    if (this.energy <= 30) {
                        this.say(this.petData.randomVocal(PetVocalsType.TIRED));
                        if (this.energy <= 10)
                            this.findNest();
                    } else if (this.happyness > 85) {
                        this.say(this.petData.randomVocal(PetVocalsType.GENERIC_HAPPY));
                    } else if (this.happyness < 15) {
                        this.say(this.petData.randomVocal(PetVocalsType.GENERIC_SAD));
                    } else if (this.levelHunger > 50) {
                        this.say(this.petData.randomVocal(PetVocalsType.HUNGRY));
                        this.eat();
                    } else if (this.levelThirst > 50) {
                        this.say(this.petData.randomVocal(PetVocalsType.THIRSTY));
                        this.drink();
                    }

                    int timeOut = Emulator.getRandom().nextInt(30);
                    this.chatTimeout = time + (timeOut < 3 ? 30 : timeOut);
                }
            }
        }
    }

    /**
     * Handles a pet command.
     *
     * @param command The command to handle.
     * @param habbo The user who issued the command.
     * @param data The data for the command.
     */
    public void handleCommand(PetCommand command, Habbo habbo, String[] data) {
        this.idleCommandTicks = 0;

        if (this.task == PetTasks.STAY) {
            this.stayStartedAt = 0;
            this.task = null;
            this.getRoomUnit().setCanWalk(true);
        }

        command.handle(this, habbo, data);


    }

    /**
     * Returns whether the pet is able to walk.
     *
     * @return true if the pet is able to walk, false otherwise.
     */
    public boolean canWalk() {
        if (this.task == null)
            return true;

        switch (this.task) {
            case DOWN:
            case FLAT:
            case HERE:
            case SIT:
            case BEG:
            case PLAY:
            case PLAY_FOOTBALL:
            case PLAY_DEAD:
            case FOLLOW:
            case JUMP:
            case STAND:
            case NEST:
            case RIDE:

                return false;
        }

        return true;
    }

    /**
     * Clears the current posture of the pet.
     */
    public void clearPosture() {
        THashMap<RoomUnitStatus, String> keys = new THashMap<>();

        if (this.roomUnit.hasStatus(RoomUnitStatus.MOVE))
            keys.put(RoomUnitStatus.MOVE, this.roomUnit.getStatus(RoomUnitStatus.MOVE));

        if (this.roomUnit.hasStatus(RoomUnitStatus.SIT))
            keys.put(RoomUnitStatus.SIT, this.roomUnit.getStatus(RoomUnitStatus.SIT));

        if (this.roomUnit.hasStatus(RoomUnitStatus.LAY))
            keys.put(RoomUnitStatus.LAY, this.roomUnit.getStatus(RoomUnitStatus.LAY));

        if (this.roomUnit.hasStatus(RoomUnitStatus.GESTURE))
            keys.put(RoomUnitStatus.GESTURE, this.roomUnit.getStatus(RoomUnitStatus.GESTURE));

        if (this.task == null) {
            boolean isDead = this.roomUnit.hasStatus(RoomUnitStatus.RIP);

            this.roomUnit.clearStatus();

            if (isDead) this.roomUnit.setStatus(RoomUnitStatus.RIP, "");
            for (Map.Entry<RoomUnitStatus, String> entry : keys.entrySet()) {
                this.roomUnit.setStatus(entry.getKey(), entry.getValue());
            }

            if (!keys.isEmpty()) this.packetUpdate = true;
        }
    }

    /**
     * Updates the pet's gesture based on its current status.
     *
     * @param time the time until the gesture should be updated again
     */
    public void updateGesture(int time) {
        this.gestureTickTimeout = time;
        if (this.energy < 30) {
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.TIRED.getKey());
            this.findNest();
        } else if (this.happyness == 100) {
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.LOVE.getKey());
        } else if (this.happyness >= 90) {
            this.randomHappyAction();
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.HAPPY.getKey());
        } else if (this.happyness <= 5) {
            this.randomSadAction();
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.SAD.getKey());
        } else if (this.levelHunger > 80) {
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.HUNGRY.getKey());
            this.eat();
        } else if (this.levelThirst > 80) {
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.THIRSTY.getKey());
            this.drink();
        } else if (this.idleCommandTicks > 240) {
            this.idleCommandTicks = 0;

            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.QUESTION.getKey());
        }
    }

    /**
     * Serializes this pet into a server message.
     *
     * @param message the server message to serialize this pet into
     */
    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.id);
        message.appendString(this.name);
        if (this.petData != null) {
            message.appendInt(this.petData.getType());
        } else {
            message.appendInt(-1);
        }
        message.appendInt(this.race);
        message.appendString(this.color);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(0);
    }

    /**
     * Makes the pet find a nest to sleep in.
     */

    public void findNest() {
        HabboItem item = this.petData.randomNest(this.room.getRoomSpecialTypes().getNests());
        this.roomUnit.setCanWalk(true);
        if (item != null) {
            this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
        } else {
            this.roomUnit.setStatus(RoomUnitStatus.LAY, this.room.getStackHeight(this.roomUnit.getX(), this.roomUnit.getY(), false) + "");
            this.say(this.petData.randomVocal(PetVocalsType.SLEEPING));
            this.task = PetTasks.DOWN;
        }
    }

    /**
     * Makes the pet drink.
     */
    public boolean drink() {
        HabboItem item = this.petData.randomDrinkItem(this.room.getRoomSpecialTypes().getPetDrinks());
        if (item != null) {
            this.roomUnit.setCanWalk(true);
            if (this.getRoomUnit().getCurrentLocation().distance(this.room.getLayout().getTile(item.getX(), item.getY())) == 0) {
                try {
                    item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                } catch (Exception ignored) {}
            } else {
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
            }
        }
        return item != null;
    }

    /**
     * Makes the pet eat.
     */
    public void eat() {
        HabboItem item = this.petData.randomFoodItem(this.room.getRoomSpecialTypes().getPetFoods());
        {
            if (item != null) {
                this.roomUnit.setCanWalk(true);
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
            }
        }
    }

    /**
     * Makes the pet search for a toy to play with.
     *
     * @return true if a toy was found, false otherwise
     */
    public boolean findToy() {
        HabboItem item = this.petData.randomToyItem(this.room.getRoomSpecialTypes().getPetToys());
        {
            if (item != null) {
                this.roomUnit.setCanWalk(true);
                if (this.getRoomUnit().getCurrentLocation().distance(this.room.getLayout().getTile(item.getX(), item.getY())) == 0) {
                    try {
                        item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                    } catch (Exception ignored) {}
                    return true;
                }
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
                return true;
            }
        }
        return false;
    }

    /**
     * Makes the pet search for a specific type of item.
     *
     * @param task the task to perform when the item is found
     * @param type the type of item to search for
     * @return true if an item was found, false otherwise
     */
    public boolean findPetItem(PetTasks task, Class<? extends HabboItem> type) {
        HabboItem item = this.petData.randomToyHabboItem(this.room.getRoomSpecialTypes().getItemsOfType(type));

            if (item != null) {
                this.roomUnit.setCanWalk(true);
                this.setTask(task);
                if (this.getRoomUnit().getCurrentLocation().distance(this.room.getLayout().getTile(item.getX(), item.getY())) == 0) {
                       try {
                            item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                        } catch (Exception ignored) {}
                       return true;
                }
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
                return true;
            }
        return false;
    }

    /**
     * Makes the pet perform a random happy action.
     */
    public void randomHappyAction() {
        if (this.petData.actionsHappy.length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.actionsHappy[Emulator.getRandom().nextInt(this.petData.actionsHappy.length)]), "");
        }
    }

    /**
     * Makes the pet perform a random sad action.
     */
    public void randomSadAction() {
        if (this.petData.actionsTired.length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.actionsTired[Emulator.getRandom().nextInt(this.petData.actionsTired.length)]), "");
        }
    }

    /**
     * Makes the pet perform a random action.
     */
    public void randomAction() {
        if (this.petData.actionsRandom.length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.actionsRandom[Emulator.getRandom().nextInt(this.petData.actionsRandom.length)]), "");
        }
    }


    /**
     * Increases the pet's experience by a given amount.
     *
     * @param amount the amount of experience to add
     */
    public void addExperience(int amount) {
        this.experience += amount;

        if (this.room != null) {
            this.room.sendComposer(new PetExperienceComposer(this, amount).compose());

            if(this.level < PetManager.experiences.length + 1 && this.experience >= PetManager.experiences[this.level - 1]) {
                this.levelUp();
            }
        }
    }

    /**
     * Levels up the pet if it has enough experience.
     */
    protected void levelUp() {
            if (this.level >= PetManager.experiences.length + 1)
                return;

            if (this.experience > PetManager.experiences[this.level - 1]) {
                this.experience = PetManager.experiences[this.level - 1];
            }
            this.level++;
            this.say(this.petData.randomVocal(PetVocalsType.LEVEL_UP));
            this.addHappyness(100);
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, "exp");
            this.gestureTickTimeout = Emulator.getIntUnixTimestamp();
            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.userId), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetLevelUp"));
            this.room.sendComposer(new PetLevelUpdatedComposer(this).compose());
        }

    /**
     * Increases the pet's thirst level by a given amount.
     *
     * @param amount the amount of thirst to add
     */
    public void addThirst(int amount) {
        this.levelThirst += amount;

        if (this.levelThirst > 100)
            this.levelThirst = 100;

        if (this.levelThirst < 0)
            this.levelThirst = 0;
    }

    /**
     * Increases the pet's hunger level by a given amount.
     *
     * @param amount the amount of hunger to add
     */
    public void addHunger(int amount) {
        this.levelHunger += amount;

        if (this.levelHunger > 100)
            this.levelHunger = 100;

        if (this.levelHunger < 0)
            this.levelHunger = 0;
    }

    /**
     * Releases the pet from its current task.
     */
    public void freeCommand() {
        this.task = null;
        this.roomUnit.setGoalLocation(this.getRoomUnit().getCurrentLocation());
        this.roomUnit.clearStatus();
        this.roomUnit.setCanWalk(true);
        this.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
    }

    /**
     * Increases the pet's happyness level when it is scratched.
     *
     * @param habbo the habbo who scratched the pet
     */
    public void scratched(Habbo habbo) {
        this.addHappyness(10);
        this.addExperience(10);
        this.addRespect();
        this.needsUpdate = true;

        if (habbo != null) {
            habbo.getHabboStats().petRespectPointsToGive--;
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new PetRespectNotificationComposer(this).compose());

            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectGiver"));
        }

        AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.userId), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectReceiver"));
    }

    /**
     * Gets the ID of the pet.
     *
     * @return the ID of the pet
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the ID of the user who owns the pet.
     *
     * @return the ID of the user who owns the pet
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Sets the ID of the user who owns the pet.
     *
     * @param userId the ID of the user who owns the pet
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the room the pet is currently in.
     *
     * @return the room the pet is currently in
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * Sets the room the pet is currently in.
     *
     * @param room the room the pet is currently in
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Gets the name of the pet.
     *
     * @return the name of the pet
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the pet.
     *
     * @param name the name of the pet
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the data for the pet's species.
     *
     * @return the data for the pet's species
     */
    public PetData getPetData() {
        return this.petData;
    }

    /**
     * Sets the data for the pet's species.
     *
     * @param petData the data for the pet's species
     */
    public void setPetData(PetData petData) {
        this.petData = petData;
    }

    /**
     * Gets the race of the pet.
     *
     * @return the race of the pet
     */
    public int getRace() {
        return this.race;
    }

    /**
     * Sets the race of the pet.
     *
     * @param race the race of the pet
     */
    public void setRace(int race) {
        this.race = race;
    }

    /**
     * Gets the color of the pet.
     *
     * @return the color of the pet
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Sets the color of the pet.
     *
     * @param color the color of the pet
     */
    public void setColor(String color) {
        this.color = color;
    }
    /**
     * Gets the happiness level of the pet.
     *
     * @return the happiness level of the pet
     */
    public int getHappyness() {
        return this.happyness;
    }

    /**
     * Sets the happiness level of the pet.
     *
     * @param happyness the happiness level of the pet
     */
    public void setHappyness(int happyness) {
        this.happyness = happyness;
    }


    /**
     * Gets the experience points of the pet.
     *
     * @return the experience points of the pet
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Sets the experience points of the pet.
     *
     * @param experience the experience points of the pet
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * Gets the energy level of the pet.
     * @return the energy level of the pet
     */
    public int getEnergy() {
        return this.energy;
    }

    /**
     * Sets the energy of the pet.
     * @param energy the new energy value for the pet
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * Gets the maximum energy that the pet can have at its current level.
     * @return the maximum energy that the pet can have at its current level
     */
    public int getMaxEnergy() {
        return this.level * 100;
    }

    /**
     * Gets the time that the pet was created.
     * @return the time that the pet was created
     */
    public int getCreated() {
        return this.created;
    }

    /**
     * Sets the time that the pet was created.
     * @param created the new time that the pet was created
     */
    public void setCreated(int created) {
        this.created = created;
    }

    /**
     * Gets the level of the pet.
     * @return the level of the pet
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Sets the level of the pet.
     * @param level the new level of the pet
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the room unit object associated with this pet.
     * @return the room unit object associated with this pet
     */
    public RoomUnit getRoomUnit() {
        return this.roomUnit;
    }

    /**
     * Sets the room unit object associated with this pet.
     * @param roomUnit the new room unit object for this pet
     */
    public void setRoomUnit(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }

    /**
     * Gets the current task of this pet.
     * @return the current task of this pet
     */
    public PetTasks getTask() {
        return this.task;
    }

    /**
     * Sets the current task of this pet.
     * @param newTask the new task for this pet
     */
    public void setTask(PetTasks newTask) {
        this.task = newTask;
    }

    /**
     * Gets whether this pet is currently muted.
     * @return true if this pet is muted, false otherwise
     */
    public boolean isMuted() {
        return this.muted;
    }

    /**
     * Sets whether this pet is currently muted.
     * @param muted true if this pet should be muted, false otherwise
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * Gets the current thirst level of this pet.
     * @return the current thirst level of this pet
     */
    public int getLevelThirst() {
        return this.levelThirst;
    }

    /**
     * Sets the current thirst level of this pet.
     * @param levelThirst the new thirst level of this pet
     */
    public void setLevelThirst(int levelThirst) {
        this.levelThirst = levelThirst;
    }

    /**
     * Gets the current hunger level of this pet.
     * @return the current hunger level of this pet
     */
    public int getLevelHunger() {
        return this.levelHunger;
    }

    /**
     * Sets the current hunger level of this pet.
     * @param levelHunger the new hunger level of this pet
     */
    public void setLevelHunger(int levelHunger) {
        this.levelHunger = levelHunger;
    }

    /**
     * Removes this pet from the room.
     */
    public void removeFromRoom() {
        removeFromRoom(false);
    }

    /**
     * Removes this pet from the room.
     * @param dontSendPackets if true, packets will not be sent to update clients
     */
    public void removeFromRoom(boolean dontSendPackets) {

        if (this.roomUnit != null && this.roomUnit.getCurrentLocation() != null) {
            this.roomUnit.getCurrentLocation().removeUnit(this.roomUnit);
        }

        if (!dontSendPackets) {
            room.sendComposer(new UserRemoveMessageComposer(this.roomUnit).compose());
            room.removePet(this.id);
        }

        this.roomUnit = null;
        this.room = null;
        this.needsUpdate = true;
    }

    /**
     * Gets the time at which this pet started staying in the room.
     * @return the time at which this pet started staying in the room
     */
    public int getStayStartedAt() {
        return stayStartedAt;
    }

    /**
     * Sets the time at which this pet started staying in the room.
     * @param stayStartedAt the new time at which this pet started staying in the room
     */
    public void setStayStartedAt(int stayStartedAt) {
        this.stayStartedAt = stayStartedAt;
    }
}
