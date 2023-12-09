package com.eu.habbo.habbohotel.rooms.chat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionMuteArea;
import com.eu.habbo.habbohotel.items.interactions.InteractionTalkingFurniture;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatType;
import com.eu.habbo.habbohotel.rooms.constants.RoomConfiguration;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.types.IRoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.RemainingMutePeriodComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UsernameTalkEvent;
import com.eu.habbo.threading.runnables.YouAreAPirate;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.regex.Pattern;

import static com.eu.habbo.habbohotel.rooms.constants.RoomConfiguration.CAUGHT_EXCEPTION;

@Getter
@Slf4j
public class RoomChatManager extends IRoomManager {
    @Setter
    private volatile boolean muted;

    private final int muteTime = Emulator.getConfig().getInt("hotel.flood.mute.time", 30);

    public RoomChatManager(Room room) {
        super(room);
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType) {
        this.talk(habbo, roomChatMessage, chatType, false);
    }

    private void showTentChatMessageOutsideTentIfPermitted(Habbo receivingHabbo, RoomChatMessage roomChatMessage, Rectangle tentRectangle) {
        if (receivingHabbo != null && receivingHabbo.hasPermissionRight(Permission.ACC_SEE_TENTCHAT) && tentRectangle != null && !RoomLayout.tileInSquare(tentRectangle, receivingHabbo.getRoomUnit().getCurrentPosition())) {
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("[" + Emulator.getTexts().getValue("hotel.room.tent.prefix") + "] " + staffChatMessage.getMessage());
            final ServerMessage staffMessage = new WhisperMessageComposer(staffChatMessage).compose();
            receivingHabbo.getClient().sendResponse(staffMessage);
        }
    }


    public void talk(final Habbo habbo, final RoomChatMessage roomChatMessage, RoomChatType chatType, boolean ignoreWired) {
        if (!habbo.getHabboStats().allowTalk()) return;

        if (habbo.getRoomUnit().isInvisible() && Emulator.getConfig().getBoolean("invisible.prevent.chat", false)) {
            if (!Emulator.getGameEnvironment().getCommandsManager().handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                habbo.whisper(Emulator.getTexts().getValue("invisible.prevent.chat.error"));
            }
            return;
        }

        if (habbo.getRoomUnit().getRoom() != room) return;

        long millis = System.currentTimeMillis();
        if (RoomConfiguration.HABBO_CHAT_DELAY && millis - habbo.getHabboStats().getLastChat() < 750) {
            return;
        }

        habbo.getHabboStats().setLastChat(millis);
        if (roomChatMessage != null && Emulator.getConfig().getBoolean("easter_eggs.enabled") && roomChatMessage.getMessage().equalsIgnoreCase("i am a pirate")) {
            habbo.getHabboStats().getChatCounter().addAndGet(1);
            Emulator.getThreading().run(new YouAreAPirate(habbo, room));
            return;
        }

        UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.TALKED, false);
        Emulator.getPluginManager().fireEvent(event);

        if (!event.isCancelled() && !event.isIdle()) {
            habbo.getRoomUnit().unIdle();
        }

        this.sendComposer(new UserTypingMessageComposer(habbo.getRoomUnit(), false).compose());

        if (roomChatMessage == null || roomChatMessage.getMessage() == null || roomChatMessage.getMessage().isEmpty())
            return;

        if (!habbo.hasPermissionRight(Permission.ACC_NOMUTE) && (!RoomConfiguration.MUTEAREA_CAN_WHISPER || chatType != RoomChatType.WHISPER)) {
            for (RoomItem area : room.getRoomSpecialTypes().getItemsOfType(InteractionMuteArea.class)) {
                if (((InteractionMuteArea) area).inSquare(habbo.getRoomUnit().getCurrentPosition())) {
                    return;
                }
            }
        }

        if (!room.getRoomWordFilterManager().getFilteredWords().isEmpty() && !habbo.hasPermissionRight(Permission.ACC_CHAT_NO_FILTER)) {
            for (String string : room.getRoomWordFilterManager().getFilteredWords()) {
                roomChatMessage.setMessage(roomChatMessage.getMessage().replaceAll("(?i)" + Pattern.quote(string), "bobba"));
            }
        }

        if (!habbo.hasPermissionRight(Permission.ACC_NOMUTE)) {
            if (this.isMuted() && !room.getRoomRightsManager().hasRights(habbo)) {
                return;
            }

            if (room.getRoomInfractionManager().isMuted(habbo)) {
                habbo.getClient().sendResponse(new RemainingMutePeriodComposer(room.getRoomInfractionManager().getMutedHabbos().get(habbo.getHabboInfo().getId()) - Emulator.getIntUnixTimestamp()));
                return;
            }
        }

        if (chatType != RoomChatType.WHISPER) {
            if (Emulator.getGameEnvironment().getCommandsManager().handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                WiredHandler.handle(WiredTriggerType.SAY_COMMAND, habbo.getRoomUnit(), habbo.getRoomUnit().getRoom(), new Object[]{roomChatMessage.getMessage()});
                roomChatMessage.isCommand = true;
                return;
            }

            if (!ignoreWired) {
                if (WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, habbo.getRoomUnit(), habbo.getRoomUnit().getRoom(), new Object[]{roomChatMessage.getMessage()})) {
                    habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(roomChatMessage.getMessage(), habbo, habbo, roomChatMessage.getBubble())));
                    return;
                }
            }
        }

        if (!habbo.hasPermissionRight(Permission.ACC_CHAT_NO_FLOOD)) {
            final int chatCounter = habbo.getHabboStats().getChatCounter().addAndGet(1);

            if (chatCounter > 3) {
                final boolean floodRights = Emulator.getConfig().getBoolean("flood.with.rights");
                final boolean hasRights = room.getRoomRightsManager().hasRights(habbo);

                if (floodRights || !hasRights) {
                    if (room.getRoomInfo().getChatProtection() == 0 || (room.getRoomInfo().getChatProtection() == 1 && chatCounter > 4) || (room.getRoomInfo().getChatProtection() == 2 && chatCounter > 5)) {
                        room.getRoomInfractionManager().floodMuteHabbo(habbo, muteTime);
                        return;
                    }
                }
            }
        }

        ServerMessage prefixMessage = null;

        if (Emulator.getPluginManager().isRegistered(UsernameTalkEvent.class, true)) {
            UsernameTalkEvent usernameTalkEvent = Emulator.getPluginManager().fireEvent(new UsernameTalkEvent(habbo, roomChatMessage, chatType));
            if (usernameTalkEvent.hasCustomComposer()) {
                prefixMessage = usernameTalkEvent.getCustomComposer();
            }
        }

        if (prefixMessage == null) {
            prefixMessage = roomChatMessage.getHabbo().getHabboInfo().getPermissionGroup().hasPrefix() ? new UserNameChangedMessageComposer(habbo, true).compose() : null;
        }
        ServerMessage clearPrefixMessage = prefixMessage != null ? new UserNameChangedMessageComposer(habbo).compose() : null;

        Rectangle tentRectangle = room.getRoomSpecialTypes().tentAt(habbo.getRoomUnit().getCurrentPosition());

        String trimmedMessage = roomChatMessage.getMessage().replaceAll("\\s+$", "");

        if (trimmedMessage.isEmpty()) trimmedMessage = " ";

        roomChatMessage.setMessage(trimmedMessage);

        if (chatType == RoomChatType.WHISPER) {
            if (roomChatMessage.getTargetHabbo() == null) {
                return;
            }

            whisper(habbo, roomChatMessage, prefixMessage, clearPrefixMessage);
        } else if (chatType == RoomChatType.TALK) {
            talk(habbo, roomChatMessage, tentRectangle, prefixMessage, clearPrefixMessage);
        } else if (chatType == RoomChatType.SHOUT) {
            shout(habbo, roomChatMessage, tentRectangle, prefixMessage, clearPrefixMessage);
        }

        if (chatType == RoomChatType.TALK || chatType == RoomChatType.SHOUT) {
            synchronized (room.getRoomUnitManager().getRoomBotManager().getCurrentBots()) {
                room.getRoomUnitManager().getRoomBotManager().getCurrentBots().values().forEach(b -> b.onUserSay(roomChatMessage));
            }

            if (roomChatMessage.getBubble().triggersTalkingFurniture()) {
                triggerTalkingFurni(habbo);
            }
        }
    }

    private void triggerTalkingFurni(Habbo habbo) {
        THashSet<RoomItem> items = room.getRoomSpecialTypes().getItemsOfType(InteractionTalkingFurniture.class);

        for (RoomItem item : items) {
            if (room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()).distance(habbo.getRoomUnit().getCurrentPosition()) <= Emulator.getConfig().getInt("furniture.talking.range")) {
                int count = Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.count", 0);

                if (count > 0) {
                    int randomValue = Emulator.getRandom().nextInt(count + 1);

                    RoomChatMessage itemMessage = new RoomChatMessage(Emulator.getTexts().getValue(item.getBaseItem().getName() + ".message." + randomValue, item.getBaseItem().getName() + ".message." + randomValue + " not found!"), habbo, RoomChatMessageBubbles.getBubble(Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.bubble", RoomChatMessageBubbles.PARROT.getType())));

                    this.sendComposer(new ChatMessageComposer(itemMessage).compose());

                    try {
                        item.onClick(habbo.getClient(), room, new Object[0]);
                        item.setExtraData("1");
                        room.updateItemState(item);

                        Emulator.getThreading().run(() -> {
                            item.setExtraData("0");
                            room.updateItemState(item);
                        }, 2000);

                        break;
                    } catch (Exception e) {
                        log.error(CAUGHT_EXCEPTION, e);
                    }
                }
            }
        }
    }

    private void shout(Habbo habbo, RoomChatMessage roomChatMessage, Rectangle tentRectangle, ServerMessage prefixMessage, ServerMessage clearPrefixMessage) {
        ServerMessage message = new ShoutMessageComposer(roomChatMessage).compose();

        for (Habbo h : room.getRoomUnitManager().getCurrentHabbos().values()) {
            // Show the message
            // If the receiving Habbo has not ignored the sending Habbo
            // AND the sending Habbo is NOT in a tent OR the receiving Habbo is in the same tent as the sending Habbo
            if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId()) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentPosition()))) {
                if (prefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                    h.getClient().sendResponse(prefixMessage);
                }
                h.getClient().sendResponse(message);
                if (clearPrefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                    h.getClient().sendResponse(clearPrefixMessage);
                }
                continue;
            }
            // Staff should be able to see the tent chat anyhow, even when not in the same tent
            showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
        }
    }

    private void talk(Habbo habbo, RoomChatMessage roomChatMessage, Rectangle tentRectangle, ServerMessage prefixMessage, ServerMessage clearPrefixMessage) {
        ServerMessage message = new ChatMessageComposer(roomChatMessage).compose();
        boolean noChatLimit = habbo.hasPermissionRight(Permission.ACC_CHAT_NO_LIMIT);

        for (Habbo h : room.getRoomUnitManager().getCurrentHabbos().values()) {
            if ((h.getRoomUnit().getCurrentPosition().distance(habbo.getRoomUnit().getCurrentPosition()) <= room.getRoomInfo().getChatDistance() || h.equals(habbo) || room.getRoomRightsManager().hasRights(h) || noChatLimit) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentPosition()))) {
                if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) {
                    if (prefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse(message);
                    if (clearPrefixMessage != null && !h.getHabboStats().isPreferOldChat()) {
                        h.getClient().sendResponse(clearPrefixMessage);
                    }
                }
                continue;
            }
            // Staff should be able to see the tent chat anyhow
            showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
        }
    }

    private void whisper(Habbo habbo, RoomChatMessage roomChatMessage, ServerMessage prefixMessage, ServerMessage clearPrefixMessage) {
        RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
        staffChatMessage.setMessage("To " + staffChatMessage.getTargetHabbo().getHabboInfo().getUsername() + ": " + staffChatMessage.getMessage());

        final ServerMessage message = new WhisperMessageComposer(roomChatMessage).compose();
        final ServerMessage staffMessage = new WhisperMessageComposer(staffChatMessage).compose();

        for (Habbo h : room.getRoomUnitManager().getCurrentHabbos().values()) {
            if (h == roomChatMessage.getTargetHabbo() || h == habbo) {
                if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) {
                    if (prefixMessage != null) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse(message);

                    if (clearPrefixMessage != null) {
                        h.getClient().sendResponse(clearPrefixMessage);
                    }
                }

                continue;
            }
            if (h.hasPermissionRight(Permission.ACC_SEE_WHISPERS)) {
                h.getClient().sendResponse(staffMessage);
            }
        }
    }

}
