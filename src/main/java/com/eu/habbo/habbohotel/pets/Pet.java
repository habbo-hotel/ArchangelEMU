package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.PetExperienceComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetLevelUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetRespectNotificationComposer;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.plugin.events.pets.PetTalkEvent;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
public class Pet implements ISerialize, Runnable {

    @Getter
    @Setter
    public int levelThirst;
    @Getter
    @Setter
    public int levelHunger;

    @Setter
    @Getter
    private boolean needsUpdate = false;

    @Setter
    @Getter
    private boolean packetUpdate = false;
    @Getter
    protected int id;
    @Getter
    @Setter
    protected int userId;
    @Getter
    @Setter
    protected Room room;
    @Getter
    @Setter
    protected String name;
    @Setter
    @Getter
    protected PetData petData;
    @Setter
    @Getter
    protected int race;
    @Getter
    @Setter
    protected String color;
    @Getter
    @Setter
    protected int happiness;
    @Getter
    @Setter
    protected int experience;
    @Setter
    @Getter
    protected int energy;

    /**
     * The respect points of the pet.
     */
    protected int respect;
    @Getter
    @Setter
    protected int created;
    @Getter
    @Setter
    protected int level;
    @Getter
    @Setter
    RoomUnit roomUnit;

    /**
     * The chat timeout of the pet.
     */
    private int chatTimeout;

    /**
     * The tick timeout of the pet.
     */
    private int tickTimeout = Emulator.getIntUnixTimestamp();
    private int happinessDelay = Emulator.getIntUnixTimestamp();
    private int gestureTickTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The random action tick timeout of the pet.
     */
    private int randomActionTickTimeout = Emulator.getIntUnixTimestamp();

    /**
     * The posture timeout of the pet.
     */
    private int postureTimeout = Emulator.getIntUnixTimestamp();
    @Getter
    @Setter
    private int stayStartedAt = 0;
    /**
     * The number of ticks that the pet has spent idle while waiting for a command.
     */
    private int idleCommandTicks = 0;

    /**
     * The number of ticks that the pet has spent free after completing a command.
     */
    private int freeCommandTicks = -1;

    @Getter
    @Setter
    private PetTasks task = PetTasks.FREE;

    @Setter
    @Getter
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
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.room = null;
        this.name = set.getString("name");
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(set.getInt("type"));
        if (this.petData == null) {
            log.error("WARNING! Missing pet data for type: " + set.getInt("type") + "! Insert a new entry into the pet_actions table for this type!");
            this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(0);
        }
        this.race = set.getInt("race");
        this.experience = set.getInt("experience");
        this.happiness = set.getInt("happiness");
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
     * @param type   the type of the pet
     * @param race   the race of the pet
     * @param color  the color of the pet
     * @param name   the name of the pet
     * @param userId the ID of the user that owns the pet
     */
    public Pet(int type, int race, String color, String name, int userId) {
        this.id = 0;
        this.userId = userId;
        this.room = null;
        this.name = name;
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(type);

        if (this.petData == null) {
            log.warn("Missing pet data for type: " + type + "! Insert a new entry into the pet_actions table for this type!");
        }

        this.race = race;
        this.color = color;
        this.experience = 0;
        this.happiness = 100;
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
            this.say(vocal.getMessage());
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


    public void addHappiness(int amount) {
        this.happiness += amount;

        if (this.happiness > 100)
            this.happiness = 100;

        if (this.happiness < 0)
            this.happiness = 0;
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
        if (this.isNeedsUpdate()) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                if (this.id > 0) {
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE users_pets SET room_id = ?, experience = ?, energy = ?, respect = ?, x = ?, y = ?, z = ?, rot = ?, hunger = ?, thirst = ?, happiness = ?, created = ? WHERE id = ?")) {
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
                        statement.setInt(11, this.happiness);
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
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }

            this.setNeedsUpdate(false);
        }
    }

    /**
     * Performs a cycle of updates for the pet. This includes updates to their walking, tasks, happiness, hunger, thirst, and energy levels.
     * It also includes updates to their gestures and random actions, as well as vocalizing if they are not muted.
     */
    public void cycle() {
        this.idleCommandTicks++;

        int time = Emulator.getIntUnixTimestamp();
        if (this.roomUnit != null && this.task != PetTasks.RIDE) {
            if (time - this.gestureTickTimeout > 5 && this.roomUnit.hasStatus(RoomUnitStatus.GESTURE)) {
                this.roomUnit.removeStatus(RoomUnitStatus.GESTURE);
                this.setPacketUpdate(true);
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


                    this.addHappiness(1);

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
                this.roomUnit.setWalkTimeOut(20 + time);

                if (this.energy >= 2)
                    this.addEnergy(-1);


                if (this.levelHunger < 100)
                    this.levelHunger++;

                if (this.levelThirst < 100)
                    this.levelThirst++;

                if (this.happiness > 0 && time - this.happinessDelay >= 30) {
                    this.happiness--;
                    this.happinessDelay = time;
                }
            }

            if (time - this.gestureTickTimeout > 15) {
                this.updateGesture(time);
            } else if (time - this.randomActionTickTimeout > 30) {
                this.randomAction();
                this.randomActionTickTimeout = time + (10 * Emulator.getRandom().nextInt(60));
            }

            if (!this.muted && this.chatTimeout <= time) {
                if (this.energy <= 30) {
                    this.say(this.petData.randomVocal(PetVocalsType.TIRED));
                    if (this.energy <= 10)
                        this.findNest();
                } else if (this.happiness > 85) {
                    this.say(this.petData.randomVocal(PetVocalsType.GENERIC_HAPPY));
                } else if (this.happiness < 15) {
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

    /**
     * Handles a pet command.
     *
     * @param command The command to handle.
     * @param habbo   The user who issued the command.
     * @param data    The data for the command.
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

        return switch (this.task) {
            case DOWN, FLAT, HERE, SIT, BEG, PLAY, PLAY_FOOTBALL, PLAY_DEAD, FOLLOW, JUMP, STAND, NEST, RIDE -> false;
            default -> true;
        };

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

            if (!keys.isEmpty()) this.setPacketUpdate(true);
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
        } else if (this.happiness == 100) {
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.LOVE.getKey());
        } else if (this.happiness >= 90) {
            this.randomHappyAction();
            this.roomUnit.setStatus(RoomUnitStatus.GESTURE, PetGestures.HAPPY.getKey());
        } else if (this.happiness <= 5) {
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
                } catch (Exception ignored) {
                }
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
        if (item != null) {
            this.roomUnit.setCanWalk(true);
            this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
        }
    }

    /**
     * Makes the pet search for a toy to play with.
     *
     * @return true if a toy was found, false otherwise
     */
    public boolean findToy() {
        HabboItem item = this.petData.randomToyItem(this.room.getRoomSpecialTypes().getPetToys());
        if (item != null) {
            this.roomUnit.setCanWalk(true);
            if (this.getRoomUnit().getCurrentLocation().distance(this.room.getLayout().getTile(item.getX(), item.getY())) == 0) {
                try {
                    item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                } catch (Exception ignored) {
                }
                return true;
            }
            this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
            return true;
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
                } catch (Exception ignored) {
                }
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
        if (this.petData.getActionsHappy().length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.getActionsHappy()[Emulator.getRandom().nextInt(this.petData.getActionsHappy().length)]), "");
        }
    }

    /**
     * Makes the pet perform a random sad action.
     */
    public void randomSadAction() {
        if (this.petData.getActionsTired().length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.getActionsTired()[Emulator.getRandom().nextInt(this.petData.getActionsTired().length)]), "");
        }
    }

    /**
     * Makes the pet perform a random action.
     */
    public void randomAction() {
        if (this.petData.getActionsRandom().length > 0) {
            this.roomUnit.setStatus(RoomUnitStatus.fromString(this.petData.getActionsRandom()[Emulator.getRandom().nextInt(this.petData.getActionsRandom().length)]), "");
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

            if (this.level < PetManager.experiences.length + 1 && this.experience >= PetManager.experiences[this.level - 1]) {
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
        this.addHappiness(100);
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
     * Increases the pet's happiness level when it is scratched.
     *
     * @param habbo the habbo who scratched the pet
     */
    public void scratched(Habbo habbo) {
        this.addHappiness(10);
        this.addExperience(10);
        this.addRespect();
        this.setNeedsUpdate(true);

        if (habbo != null) {
            habbo.getHabboStats().decreasePetRespectPointsToGive();
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new PetRespectNotificationComposer(this).compose());

            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectGiver"));
        }

        AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.userId), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectReceiver"));
    }


    public int getMaxEnergy() {
        return this.level * 100;
    }

    public void removeFromRoom() {
        removeFromRoom(false);
    }

    /**
     * Removes this pet from the room.
     *
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
        this.setNeedsUpdate(true);
    }

}
