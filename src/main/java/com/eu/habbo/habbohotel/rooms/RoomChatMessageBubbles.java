package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoomChatMessageBubbles {
    NORMAL(0, "", true, true),
    ALERT(1, "", true, true),
    BOT(2, "", true, true),
    RED(3, "", true, true),
    BLUE(4, "", true, true),
    YELLOW(5, "", true, true),
    GREEN(6, "", true, true),
    BLACK(7, "", true, true),
    FORTUNE_TELLER(8, "", false, false),
    ZOMBIE_ARM(9, "", true, false),
    SKELETON(10, "", true, false),
    LIGHT_BLUE(11, "", true, true),
    PINK(12, "", true, true),
    PURPLE(13, "", true, true),
    DARK_YEWLLOW(14, "", true, true),
    DARK_BLUE(15, "", true, true),
    HEARTS(16, "", true, true),
    ROSES(17, "", true, true),
    UNUSED(18, "", true, true), //?
    PIG(19, "", true, true),
    DOG(20, "", true, true),
    BLAZE_IT(21, "", true, true),
    DRAGON(22, "", true, true),
    STAFF(23, "", false, true),
    BATS(24, "", true, false),
    MESSENGER(25, "", true, false),
    STEAMPUNK(26, "", true, false),
    THUNDER(27, "", true, true),
    PARROT(28, "", false, false),
    PIRATE(29, "", false, false),
    BOT_GUIDE(30, "", true, true),
    BOT_RENTABLE(31, "", true, true),
    SCARY_THING(32, "", true, false),
    FRANK(33, "", true, false),
    WIRED(34, "", false, true),
    GOAT(35, "", true, false),
    SANTA(36, "", true, false),
    AMBASSADOR(37, "acc_ambassador", false, true),
    RADIO(38, "", true, false),
    UNKNOWN_39(39, "", true, false),
    UNKNOWN_40(40, "", true, false),
    UNKNOWN_41(41, "", true, false),
    UNKNOWN_42(42, "", true, false),
    UNKNOWN_43(43, "", true, false),
    UNKNOWN_44(44, "", true, false),
    UNKNOWN_45(45, "", true, false);

    @Getter
    private final int type;
    @Getter
    private final String permission;
    @Getter
    private final boolean overridable;
    private final boolean triggersTalkingFurniture;


    public static RoomChatMessageBubbles getBubble(int bubbleId) {
        try {
            return values()[bubbleId];
        } catch (Exception e) {
            return NORMAL;
        }
    }

    public boolean triggersTalkingFurniture() {
        return this.triggersTalkingFurniture;
    }
}
