package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public abstract class InteractionGameTimer extends HabboItem implements Runnable
{
    private int baseTime = 0;
    private int timeNow = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;

    public InteractionGameTimer(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        String[] data = set.getString("extra_data").split("\t");

        if (data.length >= 2)
        {
            this.baseTime = Integer.valueOf(data[1]);
            this.timeNow = this.baseTime;
        }

        if (data.length >= 1)
        {
            this.setExtradata(data[0]);
        }
    }

    public InteractionGameTimer(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void run() {
        if(this.needsUpdate() || this.needsDelete()) {
            super.run();
        }

        if(this.getRoomId() == 0)
            return;

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null || !this.isRunning || this.isPaused)
            return;

        if(this.timeNow > 0) {
            Emulator.getThreading().run(this, 1000);
            this.timeNow--;
            room.updateItem(this);
        }
        else {
            this.isRunning = false;
            this.isPaused = false;
            endGamesIfLastTimer(room);
        }
    }

    public static void endGamesIfLastTimer(Room room) {
        boolean gamesActive = false;
        for (InteractionGameTimer timer : room.getRoomSpecialTypes().getGameTimers().values()) {
            if (timer.isRunning())
                gamesActive = true;
        }

        if (!gamesActive) {
            endGames(room);
        }
    }

    public static void endGames(Room room) {
        endGames(room, false);
    }

    public static void endGames(Room room, boolean overrideTriggerWired) {

        boolean triggerWired = false;

        //end existing games
        for (Class<? extends Game> gameClass : Emulator.getGameEnvironment().getRoomManager().getGameTypes()) {
            Game game = InteractionGameTimer.getOrCreateGame(room, gameClass);
            if (!game.state.equals(GameState.IDLE)) {
                triggerWired = true;
                game.onEnd();
                game.stop();
            }
        }

        if(triggerWired) {
            WiredHandler.handle(WiredTriggerType.GAME_ENDS, null, room, new Object[]{});
        }
    }

    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }

    @Override
    public void onPlace(Room room)
    {
        if(this.baseTime == 0) {
            this.baseTime = 30;
            this.timeNow = this.baseTime;
        }

        this.setExtradata(this.timeNow + "\t" + this.baseTime);
        room.updateItem(this);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString("" + timeNow);

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(this.getExtradata().isEmpty())
        {
            this.setExtradata("0");
        }

        // if wired triggered it
        if (objects.length >= 2 && objects[1] instanceof WiredEffectType && !this.isRunning)
        {
            endGamesIfLastTimer(room);

            for(Class<? extends Game> gameClass : Emulator.getGameEnvironment().getRoomManager().getGameTypes()) {
                Game game = getOrCreateGame(room, gameClass);
                if(!game.isRunning) {
                    game.initialise();
                }
            }

            timeNow = this.baseTime;
            this.isRunning = true;
            room.updateItem(this);
            WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, room, new Object[] { });

            Emulator.getThreading().run(this);
        }
        else if(client != null)
        {
            if (!(room.hasRights(client.getHabbo()) || client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER)))
                return;

            int state = 1;

            if(objects.length >= 1 && objects[0] instanceof Integer) {
                state = (Integer) objects[0];
            }

            switch (state)
            {
                case 1:
                    if(this.isRunning) {
                        this.isPaused = !this.isPaused;

                        boolean allPaused = this.isPaused;
                        for(InteractionGameTimer timer : room.getRoomSpecialTypes().getGameTimers().values()) {
                            if(!timer.isPaused)
                                allPaused = false;
                        }

                        for(Class<? extends Game> gameClass : Emulator.getGameEnvironment().getRoomManager().getGameTypes()) {
                            Game game = getOrCreateGame(room, gameClass);
                            if(allPaused) {
                                game.pause();
                            }
                            else {
                                game.unpause();
                            }
                        }

                        if(!this.isPaused) {
                            this.isRunning = true;
                            timeNow = this.baseTime;
                            room.updateItem(this);
                            Emulator.getThreading().run(this);
                        }
                    }

                    if(!this.isRunning) {
                        endGamesIfLastTimer(room);

                        for(Class<? extends Game> gameClass : Emulator.getGameEnvironment().getRoomManager().getGameTypes()) {
                            Game game = getOrCreateGame(room, gameClass);
                            game.initialise();
                        }

                        WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, room, new Object[] { });
                        this.isRunning = true;
                        timeNow = this.baseTime;
                        room.updateItem(this);
                        Emulator.getThreading().run(this);
                    }
                    break;

                case 2:
                    if(!this.isRunning) {
                        this.increaseTimer(room);
                        return;
                    }

                    if(this.isPaused) {
                        this.isPaused = false;
                        this.isRunning = false;

                        timeNow = this.baseTime;
                        room.updateItem(this);

                        endGamesIfLastTimer(room);
                    }

                    break;

                case 3:

                    this.isPaused = false;
                    this.isRunning = false;

                    timeNow = this.baseTime;
                    room.updateItem(this);

                    boolean gamesActive = false;
                    for (InteractionGameTimer timer : room.getRoomSpecialTypes().getGameTimers().values()) {
                        if (timer.isRunning())
                            gamesActive = true;
                    }

                    if (!gamesActive) {
                        endGames(room);
                    }
                    break;
            }
        }

        super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    public static Game getOrCreateGame(Room room, Class<? extends Game> gameClass)
    {
        Game game = (gameClass.cast(room.getGame(gameClass)));

        if (game == null)  {
            try {
                game = gameClass.getDeclaredConstructor(Room.class).newInstance(room);
                room.addGame(game);
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }

        return game;
    }

    private void increaseTimer(Room room)
    {
        if(this.isRunning)
            return;

        this.needsUpdate(true);

        switch(this.baseTime)
        {
            case 0:     this.baseTime = 30; break;
            case 30:    this.baseTime = 60; break;
            case 60:    this.baseTime = 120; break;
            case 120:   this.baseTime = 180; break;
            case 180:   this.baseTime = 300; break;
            case 300:   this.baseTime = 600; break;
            //case 600:   this.baseTime = 0; break;

            default:
                this.baseTime = 30;
        }

        this.timeNow = this.baseTime;
        room.updateItem(this);
        this.needsUpdate(true);
    }

    @Override
    public String getDatabaseExtraData()
    {
        return this.getExtradata() + "\t" + this.baseTime;
    }

    public abstract Class<? extends Game> getGameType();

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getTimeNow() {
        return timeNow;
    }

    public void setTimeNow(int timeNow) {
        this.timeNow = timeNow;
    }
}
