package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.rooms.constants.RoomState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SaveRoomSettingsEvent extends MessageHandler {


    @Override
    public void handle() {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(roomId);

        if (room != null) {
            if (room.getRoomInfo().isRoomOwner(this.client.getHabbo())) {
                String name = this.packet.readString();

                if (name.trim().isEmpty() || name.length() > 60) {
                    this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.ROOM_NAME_MISSING, ""));
                    return;
                }

                if (!Emulator.getGameEnvironment().getWordFilter().filter(name, this.client.getHabbo()).equals(name)) {
                    this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.ROOM_NAME_BADWORDS, ""));
                    return;
                }

                String description = this.packet.readString();

                if (description.length() > 255) {
                    return;
                }

                if (!Emulator.getGameEnvironment().getWordFilter().filter(description, this.client.getHabbo()).equals(description)) {
                    this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.ROOM_DESCRIPTION_BADWORDS, ""));
                    return;
                }

                RoomState state = RoomState.values()[this.packet.readInt() % RoomState.values().length];

                String password = this.packet.readString();
                if (state == RoomState.PASSWORD && password.isEmpty() && (room.getRoomInfo().getPassword() == null || room.getRoomInfo().getPassword().isEmpty())) {
                    this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.PASSWORD_REQUIRED, ""));
                    return;
                }

                int usersMax = this.packet.readInt();
                int categoryId = this.packet.readInt();
                StringBuilder tags = new StringBuilder();
                Set<String> uniqueTags = new HashSet<>();
                int count = Math.min(this.packet.readInt(), 2);
                for (int i = 0; i < count; i++) {
                    String tag = this.packet.readString();

                    if (tag.length() > 15) {
                        this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.TAGS_TOO_LONG, ""));
                        return;
                    }
                    if(!uniqueTags.contains(tag)) {
                        uniqueTags.add(tag);
                        tags.append(tag).append(";");
                    }
                }

                if (!Emulator.getGameEnvironment().getWordFilter().filter(tags.toString(), this.client.getHabbo()).equals(tags.toString())) {
                    this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.ROOM_TAGS_BADWWORDS, ""));
                    return;
                }


                if (tags.length() > 0) {
                    for (String s : Emulator.getConfig().getValue("hotel.room.tags.staff").split(";")) {
                        if (tags.toString().contains(s)) {
                            this.client.sendResponse(new RoomSettingsSaveErrorComposer(room.getRoomInfo().getId(), RoomSettingsSaveErrorComposer.RESTRICTED_TAGS, "1"));
                            return;
                        }
                    }
                }

                room.getRoomInfo().setName(name);
                room.getRoomInfo().setDescription(description);
                room.getRoomInfo().setState(state);
                if (!password.isEmpty()) room.getRoomInfo().setPassword(password);
                room.getRoomInfo().setMaxUsers(usersMax);


                if (Emulator.getGameEnvironment().getRoomManager().hasCategory(categoryId, this.client.getHabbo()))
                    room.getRoomInfo().setCategory(Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId));
                else {
                    RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId);

                    String message;

                    if (category == null) {
                        message = Emulator.getTexts().getValue("scripter.warning.roomsettings.category.nonexisting").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername());
                    } else {
                        message = Emulator.getTexts().getValue("scripter.warning.roomsettings.category.permission").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%category%", Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId) + "");
                    }

                    ScripterManager.scripterDetected(this.client, message);
                    log.info(message);
                }


                room.getRoomInfo().setTags(tags.toString());
                int tradeMode = this.packet.readInt();
                room.getRoomInfo().setTradeMode(tradeMode);
                room.getRoomInfo().setAllowPets(this.packet.readBoolean());

                if(!room.getRoomInfo().isAllowPets()) {
                    room.getRoomUnitManager().removeAllPetsExceptRoomOwner();
                }

                boolean allowPetsEat = this.packet.readBoolean();
                room.getRoomInfo().setAllowPetsEat(allowPetsEat);
                boolean allowWalkthrough = this.packet.readBoolean();
                room.getRoomInfo().setAllowWalkthrough(allowWalkthrough);
                boolean hideWall = this.packet.readBoolean();
                room.getRoomInfo().setHideWalls(hideWall);
                room.getRoomInfo().setWallThickness(this.packet.readInt());
                room.getRoomInfo().setFloorThickness(this.packet.readInt());
                int muteOption = this.packet.readInt();
                room.getRoomInfo().setWhoCanMuteOption(muteOption);
                int kickOption = this.packet.readInt();
                room.getRoomInfo().setWhoCanKickOption(kickOption);
                int banOption = this.packet.readInt();
                room.getRoomInfo().setWhoCanBanOption(banOption);
                int chatMode = this.packet.readInt();
                room.getRoomInfo().setChatMode(chatMode);
                int chatWeight = this.packet.readInt();
                room.getRoomInfo().setChatWeight(chatWeight);
                int chatSpeed = this.packet.readInt();
                room.getRoomInfo().setChatSpeed(chatSpeed);
                int chatDistance = Math.abs(this.packet.readInt());
                room.getRoomInfo().setChatDistance(chatDistance);
                int chatProtection = this.packet.readInt();
                room.getRoomInfo().setChatProtection(chatProtection);
                room.setNeedsUpdate(true);

                room.sendComposer(new RoomVisualizationSettingsComposer(room).compose());
                room.sendComposer(new RoomChatSettingsMessageComposer(room).compose());
                room.sendComposer(new RoomInfoUpdatedComposer(room).compose());
                this.client.sendResponse(new RoomSettingsSavedComposer(room));
            }
        }
    }
}