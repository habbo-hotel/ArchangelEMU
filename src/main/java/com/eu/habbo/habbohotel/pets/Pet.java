package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.chat.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.pets.entities.RoomPet;
import com.eu.habbo.habbohotel.units.Unit;
import com.eu.habbo.habbohotel.users.Habbo;
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
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class Pet extends Unit implements ISerialize, Runnable {
    protected final int id;
    protected int userId;
    protected Room room;
    protected String name;
    protected PetData petData;
    protected int race;
    protected String color;
    protected int happiness;
    protected int experience;
    protected int energy;
    protected int respect;
    protected int created;
    protected int level;
    private int chatTimeout;
    private int tickTimeout = Emulator.getIntUnixTimestamp();
    private int happinessDelay = Emulator.getIntUnixTimestamp();
    private int gestureTickTimeout = Emulator.getIntUnixTimestamp();
    private int randomActionTickTimeout = Emulator.getIntUnixTimestamp();
    private int postureTimeout = Emulator.getIntUnixTimestamp();
    private int stayStartedAt = 0;
    private int idleCommandTicks = 0;
    private int freeCommandTicks = -1;
    private PetTasks task = PetTasks.FREE;
    private boolean muted = false;
    private final RoomPet roomUnit;
    public int levelThirst;
    public int levelHunger;
    private boolean sqlUpdateNeeded = false;
    private boolean packetUpdate = false;

    /**
     * Creates a new pet using the given result set, which should contain data retrieved from a
     * database.
     *
     * @param set the result set containing the pet data
     * @throws SQLException if an error occurs while reading from the result set
     */
    public Pet(ResultSet set) throws SQLException {
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

        this.roomUnit = new RoomPet();
        this.roomUnit.setUnit(this);
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

        this.roomUnit = new RoomPet();
        this.roomUnit.setUnit(this);
    }

    /**
     * Makes the pet say the given message in the room it is currently in.
     *
     * @param message the message to be said
     */
    protected void say(String message) {
        if (this.getRoomUnit() != null && this.room != null && !message.isEmpty()) {
            RoomChatMessage chatMessage = new RoomChatMessage(message, this.getRoomUnit(), RoomChatMessageBubbles.NORMAL);
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
        if (this.sqlUpdateNeeded) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_pets SET room_id = ?, experience = ?, energy = ?, respect = ?, x = ?, y = ?, z = ?, rot = ?, hunger = ?, thirst = ?, happiness = ?, created = ? WHERE id = ?")) {
                statement.setInt(1, (this.room == null ? 0 : this.room.getRoomInfo().getId()));
                statement.setInt(2, this.experience);
                statement.setInt(3, this.energy);
                statement.setInt(4, this.respect);
                statement.setInt(5, this.getRoomUnit().getCurrentPosition() != null ? this.getRoomUnit().getCurrentPosition().getX() : 0);
                statement.setInt(6, this.getRoomUnit().getCurrentPosition() != null ? this.getRoomUnit().getCurrentPosition().getY() : 0);
                statement.setDouble(7, this.getRoomUnit().getCurrentPosition()!= null ? this.getRoomUnit().getCurrentZ() : 0.0);
                statement.setInt(8, this.getRoomUnit().getCurrentPosition() != null ? this.getRoomUnit().getBodyRotation().getValue() : 0);
                statement.setInt(9, this.levelHunger);
                statement.setInt(10, this.levelThirst);
                statement.setInt(11, this.happiness);
                statement.setInt(12, this.created);
                statement.setInt(13, this.id);
                statement.execute();
                this.sqlUpdateNeeded = false;
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    /**
     * Performs a cycle of updates for the pet. This includes updates to their walking, tasks, happiness, hunger, thirst, and energy levels.
     * It also includes updates to their gestures and random actions, as well as vocalizing if they are not muted.
     */
    public void cycle() {
        this.idleCommandTicks++;

        int currentTime = Emulator.getIntUnixTimestamp();

        if (this.getRoomUnit() != null && this.task != PetTasks.RIDE) {
            if (currentTime - this.gestureTickTimeout > 5 && this.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE)) {
                this.getRoomUnit().removeStatus(RoomUnitStatus.GESTURE);
            }

            if (currentTime - this.postureTimeout > 1 && this.task == null) {
                this.clearPosture();
                this.postureTimeout = currentTime + 120;
            }

            if (this.freeCommandTicks > 0) {
                this.freeCommandTicks--;

                if (this.freeCommandTicks == 0) {
                    this.freeCommand();
                }
            }

            if (!this.getRoomUnit().isWalking()) {
                if (this.getRoomUnit().getWalkTimeOut() < currentTime && this.canWalk()) {
                    RoomTile tile = this.room.getRandomWalkableTile();

                    if (tile != null) {
                        this.getRoomUnit().walkTo(tile);
                    }
                }

                if (this.task == PetTasks.NEST || this.task == PetTasks.DOWN) {
                    if (this.levelHunger > 0)
                        this.levelHunger--;

                    if (this.levelThirst > 0)
                        this.levelThirst--;


                    this.addHappiness(1);

                    if (this.energy == PetManager.maxEnergy(this.level)) {
                        this.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
                        this.getRoomUnit().setCanWalk(true);
                        this.getRoomUnit().walkTo(this.room.getRandomWalkableTile());
                        this.task = null;
                        this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.ENERGY.getKey());
                        this.gestureTickTimeout = currentTime;
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
                this.getRoomUnit().setWalkTimeOut(20 + currentTime);

                if (this.energy >= 2)
                    this.addEnergy(-1);


                if (this.levelHunger < 100)
                    this.levelHunger++;

                if (this.levelThirst < 100)
                    this.levelThirst++;

                if (this.happiness > 0 && currentTime - this.happinessDelay >= 30) {
                    this.happiness--;
                    this.happinessDelay = currentTime;
                }
            }

            if (currentTime - this.gestureTickTimeout > 15) {
                this.updateGesture(currentTime);
            } else if (currentTime - this.randomActionTickTimeout > 30) {
                this.randomAction();
                this.randomActionTickTimeout = currentTime + (10 * Emulator.getRandom().nextInt(60));
            }

            if (!this.muted && this.chatTimeout <= currentTime) {
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
                this.chatTimeout = currentTime + (timeOut < 3 ? 30 : timeOut);
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

        if (this.getRoomUnit().hasStatus(RoomUnitStatus.MOVE))
            keys.put(RoomUnitStatus.MOVE, this.getRoomUnit().getStatus(RoomUnitStatus.MOVE));

        if (this.getRoomUnit().hasStatus(RoomUnitStatus.SIT))
            keys.put(RoomUnitStatus.SIT, this.getRoomUnit().getStatus(RoomUnitStatus.SIT));

        if (this.getRoomUnit().hasStatus(RoomUnitStatus.LAY))
            keys.put(RoomUnitStatus.LAY, this.getRoomUnit().getStatus(RoomUnitStatus.LAY));

        if (this.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE))
            keys.put(RoomUnitStatus.GESTURE, this.getRoomUnit().getStatus(RoomUnitStatus.GESTURE));

        if (this.task == null) {
            boolean isDead = this.getRoomUnit().hasStatus(RoomUnitStatus.RIP);

            this.getRoomUnit().clearStatuses();

            if (isDead) this.getRoomUnit().addStatus(RoomUnitStatus.RIP, "");
            for (Map.Entry<RoomUnitStatus, String> entry : keys.entrySet()) {
                this.getRoomUnit().addStatus(entry.getKey(), entry.getValue());
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
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.TIRED.getKey());
            this.findNest();
        } else if (this.happiness == 100) {
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.LOVE.getKey());
        } else if (this.happiness >= 90) {
            this.randomHappyAction();
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.HAPPY.getKey());
        } else if (this.happiness <= 5) {
            this.randomSadAction();
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.SAD.getKey());
        } else if (this.levelHunger > 80) {
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.HUNGRY.getKey());
            this.eat();
        } else if (this.levelThirst > 80) {
            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.THIRSTY.getKey());
            this.drink();
        } else if (this.idleCommandTicks > 240) {
            this.idleCommandTicks = 0;

            this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, PetGestures.QUESTION.getKey());
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
        RoomItem item = this.petData.randomNest(this.room.getRoomSpecialTypes().getNests());
        this.getRoomUnit().setCanWalk(true);
        if (item != null) {
            this.getRoomUnit().walkTo(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()));
        } else {
            if(this instanceof HorsePet horsePet && horsePet.hasSaddle()) {
                return;
            }

            this.getRoomUnit().addStatus(RoomUnitStatus.LAY, this.room.getStackHeight(this.getRoomUnit().getCurrentPosition().getX(), this.getRoomUnit().getCurrentPosition().getY(), false) + "");
            this.say(this.petData.randomVocal(PetVocalsType.SLEEPING));
            this.task = PetTasks.DOWN;
        }
    }

    /**
     * Makes the pet drink.
     */
    public boolean drink() {
        RoomItem item = this.petData.randomDrinkItem(this.room.getRoomSpecialTypes().getPetDrinks());
        if (item != null) {
            this.getRoomUnit().setCanWalk(true);
            if (this.getRoomUnit().getCurrentPosition().distance(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY())) == 0) {
                try {
                    item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                } catch (Exception ignored) {
                }
            } else {
                this.getRoomUnit().walkTo(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()));
            }
        }
        return item != null;
    }

    /**
     * Makes the pet eat.
     */
    public void eat() {
        RoomItem item = this.petData.randomFoodItem(this.room.getRoomSpecialTypes().getPetFoods());
        if (item != null) {
            this.getRoomUnit().setCanWalk(true);
            this.getRoomUnit().walkTo(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()));
        }
    }

    /**
     * Makes the pet search for a toy to play with.
     *
     * @return true if a toy was found, false otherwise
     */
    public boolean findToy() {
        RoomItem item = this.petData.randomToyItem(this.room.getRoomSpecialTypes().getPetToys());
        if (item != null) {
            this.getRoomUnit().setCanWalk(true);
            if (this.getRoomUnit().getCurrentPosition().distance(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY())) == 0) {
                try {
                    item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                } catch (Exception ignored) {
                }
                return true;
            }
            this.getRoomUnit().walkTo(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()));
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
    public boolean findPetItem(PetTasks task, Class<? extends RoomItem> type) {
        RoomItem item = this.petData.randomToyHabboItem(this.room.getRoomSpecialTypes().getItemsOfType(type));

        if (item != null) {
            this.getRoomUnit().setCanWalk(true);
            this.setTask(task);
            if (this.getRoomUnit().getCurrentPosition().distance(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY())) == 0) {
                try {
                    item.onWalkOn(this.getRoomUnit(), this.getRoom(), null);
                } catch (Exception ignored) {
                }
                return true;
            }
            this.getRoomUnit().walkTo(this.room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()));
            return true;
        }
        return false;
    }

    /**
     * Makes the pet perform a random happy action.
     */
    public void randomHappyAction() {
        if (this.petData.getActionsHappy().length > 0) {
            this.getRoomUnit().addStatus(RoomUnitStatus.fromString(this.petData.getActionsHappy()[Emulator.getRandom().nextInt(this.petData.getActionsHappy().length)]), "");
        }
    }

    /**
     * Makes the pet perform a random sad action.
     */
    public void randomSadAction() {
        if (this.petData.getActionsTired().length > 0) {
            this.getRoomUnit().addStatus(RoomUnitStatus.fromString(this.petData.getActionsTired()[Emulator.getRandom().nextInt(this.petData.getActionsTired().length)]), "");
        }
    }

    /**
     * Makes the pet perform a random action.
     */
    public void randomAction() {
        if (this.petData.getActionsRandom().length > 0) {
            this.getRoomUnit().addStatus(RoomUnitStatus.fromString(this.petData.getActionsRandom()[Emulator.getRandom().nextInt(this.petData.getActionsRandom().length)]), "");
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
        this.getRoomUnit().addStatus(RoomUnitStatus.GESTURE, "exp");
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
        this.getRoomUnit().walkTo(this.getRoomUnit().getCurrentPosition());
        this.getRoomUnit().clearStatuses();
        this.getRoomUnit().setCanWalk(true);
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
        this.setSqlUpdateNeeded(true);

        if (habbo != null) {
            habbo.getHabboStats().decreasePetRespectPointsToGive();
            habbo.getRoomUnit().getRoom().sendComposer(new PetRespectNotificationComposer(this).compose());

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

        if (this.getRoomUnit() != null && this.getRoomUnit().getCurrentPosition() != null) {
            this.getRoomUnit().getCurrentPosition().removeUnit(this.getRoomUnit());
        }

        if (!dontSendPackets) {
            room.sendComposer(new UserRemoveMessageComposer(this.getRoomUnit()).compose());
            room.getRoomUnitManager().getRoomPetManager().removePet(this.id);
        }

        this.roomUnit.setRoom(null);
        this.room = null;
        this.setSqlUpdateNeeded(true);
    }

}
