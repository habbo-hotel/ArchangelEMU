package com.eu.habbo.habbohotel.rooms.constants;

import com.eu.habbo.habbohotel.rooms.Room;

import java.util.Comparator;

public class RoomConfiguration {
    public static final double MAXIMUM_FURNI_HEIGHT = 40d;
    public static final String CAUGHT_EXCEPTION = "Caught exception";
    public static final Comparator<Room> SORT_SCORE = Comparator.comparingInt(room -> room.getRoomInfo().getScore());
    public static final Comparator<Room> SORT_ID = Comparator.comparingInt(room -> room.getRoomInfo().getId());
    public static final Comparator<Room> SORT_USERS_COUNT = Comparator.comparingInt((Room room) -> room.getRoomUnitManager().getRoomHabbosCount()).thenComparing(SORT_ID);
    public static boolean HABBO_CHAT_DELAY = false;
    public static int MAXIMUM_BOTS = 10;
    public static int MAXIMUM_PETS = 10;
    public static int MAXIMUM_FURNI = 2500;
    public static int MAXIMUM_POSTITNOTES = 200;
    public static int HAND_ITEM_TIME = 10;
    public static int IDLE_CYCLES = 240;
    public static int IDLE_CYCLES_KICK = 480;
    public static String PREFIX_FORMAT = "[<font color=\"%color%\">%prefix%</font>] ";
    public static int ROLLERS_MAXIMUM_ROLL_AVATARS = 1;
    public static boolean MUTEAREA_CAN_WHISPER = false;
}
