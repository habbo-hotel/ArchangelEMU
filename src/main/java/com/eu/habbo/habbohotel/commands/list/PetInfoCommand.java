package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class PetInfoCommand extends Command {
    public PetInfoCommand() {
        super("cmd_pet_info");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length <= 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_pet_info.pet_not_found"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return false;

        String name = params[1];

        gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentPets().forEachEntry((a, pet) -> {
            if (pet.getName().equalsIgnoreCase(name)) {
                gameClient.getHabbo().alert("" +
                        getTextsValue("commands.generic.cmd_pet_info.title") + ": " + pet.getName() + "\r\n" +
                        getTextsValue("generic.pet.id") + ": " + pet.getId() + "\r" +
                        getTextsValue("generic.pet.name") + ": " + pet.getName() + "\r" +
                        getTextsValue("generic.pet.age") + ": " + pet.daysAlive() + " " + getTextsValue("generic.pet.days.alive") + "\r" +
                        getTextsValue("generic.pet.level") + ": " + pet.getLevel() + "\r" +
                        "\r" +
                        getTextsValue("commands.generic.cmd_pet_info.stats") + "\r\n" +
                        getTextsValue("generic.pet.scratches") + ": " + pet.getRespect() + "\r" +
                        getTextsValue("generic.pet.energy") + ": " + pet.getEnergy() + "/" + PetManager.maxEnergy(pet.getLevel()) + "\r" +
                        getTextsValue("generic.pet.happiness") + ": " + pet.getHappiness() + "\r" +
                        getTextsValue("generic.pet.level.thirst") + ": " + pet.levelThirst + "\r" +
                        getTextsValue("generic.pet.level.hunger") + ": " + pet.levelHunger + "\r" +
                        getTextsValue("generic.pet.current_action") + ": " + (pet.getTask() == null ? getTextsValue("generic.nothing") : pet.getTask().name()) + "\r" +
                        getTextsValue("generic.can.walk") + ": " + (pet.getRoomUnit().canWalk() ? getTextsValue("generic.yes") : getTextsValue("generic.no")) + ""
                );
            }

            return true;
        });
        return true;
    }
}
