package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.CommandLog;
import com.eu.habbo.habbohotel.commands.list.*;
import com.eu.habbo.habbohotel.commands.list.badge.*;
import com.eu.habbo.habbohotel.commands.list.bans.*;
import com.eu.habbo.habbohotel.commands.list.credits.*;
import com.eu.habbo.habbohotel.commands.list.gift.*;
import com.eu.habbo.habbohotel.commands.list.pixels.*;
import com.eu.habbo.habbohotel.commands.list.points.*;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.permissions.PermissionCommand;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.messages.outgoing.rooms.users.UserTypingMessageComposer;
import com.eu.habbo.plugin.events.users.UserCommandEvent;
import com.eu.habbo.plugin.events.users.UserExecuteCommandEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandsManager {
    @Getter
    private static final Map<String, Command> commands = new HashMap<>();

    public CommandsManager() {
        long millis = System.currentTimeMillis();
        this.reloadCommands();
        log.info("Command Handler -> Loaded! ({} MS)", System.currentTimeMillis() - millis);
    }

    public static void addCommand(Command command) {
        if(command != null) {
            commands.put(command.getName(), command);
        }
    }

    public static void addCommand(Class<? extends Command> commandClass) {
        try {
            addCommand(commandClass.getDeclaredConstructor().newInstance());
            log.debug("Added command: {}", commandClass.getName());
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    public boolean handleCommand(GameClient gameClient, String commandLine) {
        if (gameClient == null) {
            return false;
        }

        if (commandLine.startsWith(":")) {
            return this.handleChatCommand(gameClient, commandLine.substring(1));
        } else {
            return this.handlePetCommand(gameClient, commandLine);
        }
    }

    private boolean handleChatCommand(GameClient gameClient, String commandLine) {
        String[] parts = commandLine.split(" ");

        if(parts.length < 1) {
            return false;
        }

        String commandKey = parts[0];
        Room currentRoom = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        boolean hasRights = currentRoom != null &&
                (currentRoom.hasRights(gameClient.getHabbo())) ||
                gameClient.getHabbo().hasRight(Permission.ACC_PLACEFURNI) ||
                currentRoom.getGuildId() > 0 && currentRoom.getGuildRightLevel(gameClient.getHabbo()).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS);


        if (!gameClient.getHabbo().canExecuteCommand(commandKey, hasRights)) {
            return false;
        }

        PermissionCommand permissionCommand = Emulator.getGameEnvironment().getPermissionsManager().getCommandByKey(commandKey);

        if (permissionCommand == null || !commands.containsKey(permissionCommand.getName())) {
            return false;
        }

        Command command = commands.get(permissionCommand.getName());

        if(command == null) {
            return false;
        }

        try {
            UserExecuteCommandEvent userExecuteCommandEvent = new UserExecuteCommandEvent(gameClient.getHabbo(), command, parts);
            Emulator.getPluginManager().fireEvent(userExecuteCommandEvent);

            if(userExecuteCommandEvent.isCancelled()) {
                return userExecuteCommandEvent.isSuccess();
            }

            if (currentRoom != null) {
                currentRoom.sendComposer(new UserTypingMessageComposer(gameClient.getHabbo().getRoomUnit(), false).compose());
            }

            UserCommandEvent event = new UserCommandEvent(gameClient.getHabbo(), parts, command.handle(gameClient, parts));
            Emulator.getPluginManager().fireEvent(event);

            if(gameClient.getHabbo().getHabboInfo().getPermissionGroup().isLogEnabled()) {
                Emulator.getDatabaseLogger().store(new CommandLog(gameClient.getHabbo().getHabboInfo().getId(), command, commandLine, event.succes));
            }

            return event.succes;
        }
        catch (Exception exception) {
            log.error("Caught exception", exception);
        }

        return false;
    }

    private boolean handlePetCommand(GameClient gameClient, String commandLine) {
        String[] args = commandLine.split(" ");

        if (args.length <= 1 || gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return false;
        }

        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

        if (room.getCurrentPets().isEmpty()) {
            return false;
        }

        for(Pet pet : room.getCurrentPets().valueCollection()) {
            if (pet != null && pet.getName().equalsIgnoreCase(args[0])) {
                StringBuilder commandBuilder = new StringBuilder();

                for (int i = 1; i < args.length; i++) {
                    commandBuilder.append(args[i]).append(" ");
                }

                String commandKey = commandBuilder.toString().trim();

                for (PetCommand command : pet.getPetData().getPetCommands()) {
                    if (command.getKey().equalsIgnoreCase(commandKey)) {
                        if (pet instanceof RideablePet rideablePet && rideablePet.getRider() != null && rideablePet.getRider().getHabboInfo().getId() == gameClient.getHabbo().getHabboInfo().getId()) {
                            rideablePet.getRider().getHabboInfo().dismountPet();
                            break;
                        }

                        if (command.getLevel() <= pet.getLevel()) {
                            pet.handleCommand(command, gameClient.getHabbo(), args);
                        } else {
                            pet.say(pet.getPetData().randomVocal(PetVocalsType.UNKNOWN_COMMAND));
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void reloadCommands() {
        addCommand(new AboutCommand());
        addCommand(new AddYoutubePlaylistCommand());
        addCommand(new AlertCommand());
        addCommand(new AllowTradingCommand());
        addCommand(new ArcturusCommand());
        addCommand(new BadgeCommand());
        addCommand(new BanCommand());
        addCommand(new BlockAlertCommand());
        addCommand(new BotsCommand());
        addCommand(new CalendarCommand());
        addCommand(new ChangeNameCommand());
        addCommand(new ChatTypeCommand());
        addCommand(new CommandsCommand());
        addCommand(new ControlCommand());
        addCommand(new CoordsCommand());
        addCommand(new CreditsCommand());
        addCommand(new DiagonalCommand());
        addCommand(new DisconnectCommand());
        addCommand(new EjectAllCommand());
        addCommand(new EmptyInventoryCommand());
        addCommand(new EmptyBotsInventoryCommand());
        addCommand(new EmptyPetsInventoryCommand());
        addCommand(new EnableCommand());
        addCommand(new EventCommand());
        addCommand(new FacelessCommand());
        addCommand(new FastwalkCommand());
        addCommand(new FilterWordCommand());
        addCommand(new FreezeBotsCommand());
        addCommand(new FreezeCommand());
        addCommand(new GiftCommand());
        addCommand(new GiveRankCommand());
        addCommand(new HabnamCommand());
        addCommand(new HandItemCommand());
        addCommand(new HappyHourCommand());
        addCommand(new HideWiredCommand());
        addCommand(new HotelAlertCommand());
        addCommand(new HotelAlertLinkCommand());
        addCommand(new InvisibleCommand());
        addCommand(new IPBanCommand());
        addCommand(new LayCommand());
        addCommand(new MachineBanCommand());
        addCommand(new MassBadgeCommand());
        addCommand(new RoomBadgeCommand());
        addCommand(new MassCreditsCommand());
        addCommand(new MassGiftCommand());
        addCommand(new MassPixelsCommand());
        addCommand(new MassPointsCommand());
        addCommand(new MimicCommand());
        addCommand(new MoonwalkCommand());
        addCommand(new MultiCommand());
        addCommand(new MuteBotsCommand());
        addCommand(new MuteCommand());
        addCommand(new MutePetsCommand());
        addCommand(new PetInfoCommand());
        addCommand(new PickallCommand());
        addCommand(new PixelCommand());
        addCommand(new PluginsCommand());
        addCommand(new PointsCommand());
        addCommand(new PromoteTargetOfferCommand());
        addCommand(new PullCommand());
        addCommand(new PushCommand());
        addCommand(new RedeemCommand());
        addCommand(new ReloadRoomCommand());
        addCommand(new RightsCommand());
        addCommand(new RoomAlertCommand());
        addCommand(new RoomBundleCommand());
        addCommand(new RoomCreditsCommand());
        addCommand(new RoomDanceCommand());
        addCommand(new RoomEffectCommand());
        addCommand(new RoomItemCommand());
        addCommand(new RoomKickCommand());
        addCommand(new RoomMuteCommand());
        addCommand(new RoomPixelsCommand());
        addCommand(new RoomPointsCommand());
        addCommand(new RoomSitCommand());
        addCommand(new SayAllCommand());
        addCommand(new SayCommand());
        addCommand(new SetMaxCommand());
        addCommand(new SetPollCommand());
        addCommand(new SetSpeedCommand());
        addCommand(new ShoutAllCommand());
        addCommand(new ShoutCommand());
        addCommand(new ShutdownCommand());
        addCommand(new SitCommand());
        addCommand(new StandCommand());
        addCommand(new SoftKickCommand());
        addCommand(new SubscriptionCommand());
        addCommand(new StaffAlertCommand());
        addCommand(new StaffOnlineCommand());
        addCommand(new StalkCommand());
        addCommand(new SummonCommand());
        addCommand(new SummonRankCommand());
        addCommand(new SuperbanCommand());
        addCommand(new SuperPullCommand());
        addCommand(new TakeBadgeCommand());
        addCommand(new TeleportCommand());
        addCommand(new TestCommand());
        addCommand(new TransformCommand());
        addCommand(new UnbanCommand());
        addCommand(new UnloadRoomCommand());
        addCommand(new UnmuteCommand());
        addCommand(new UpdateAchievements());
        addCommand(new UpdateBotsCommand());
        addCommand(new UpdateCalendarCommand());
        addCommand(new UpdateCatalogCommand());
        addCommand(new UpdateConfigCommand());
        addCommand(new UpdateGuildPartsCommand());
        addCommand(new UpdateHotelViewCommand());
        addCommand(new UpdateItemsCommand());
        addCommand(new UpdateNavigatorCommand());
        addCommand(new UpdatePermissionsCommand());
        addCommand(new UpdatePetDataCommand());
        addCommand(new UpdatePluginsCommand());
        addCommand(new UpdatePollsCommand());
        addCommand(new UpdateTextsCommand());
        addCommand(new UpdateWordFilterCommand());
        addCommand(new UpdateYoutubePlaylistsCommand());
        addCommand(new UserInfoCommand());
        addCommand(new WordQuizCommand());
    }

    public void dispose() {
        commands.clear();
        log.info("Command Handler -> Disposed!");
    }
}
